package de.jensklingenberg.ktorfit.poetspec

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import de.jensklingenberg.ktorfit.KtorfitOptions
import de.jensklingenberg.ktorfit.model.ClassData
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.PROPERTIES_NOT_SUPPORTED
import de.jensklingenberg.ktorfit.model.baseUrl
import de.jensklingenberg.ktorfit.model.converterHelper
import de.jensklingenberg.ktorfit.model.getConverters
import de.jensklingenberg.ktorfit.model.httpClient
import de.jensklingenberg.ktorfit.model.internalApi
import de.jensklingenberg.ktorfit.model.ktorfitClass
import de.jensklingenberg.ktorfit.model.toClassName

/**
 * Transform a [ClassData] to a [TypeSpec] for KotlinPoet
 */
fun ClassData.getImplClassSpec(
    resolver: Resolver,
    ktorfitOptions: KtorfitOptions,
): TypeSpec {
    val classData = this

    val implClassProperties =
        classData.properties.map { property ->
            propertySpec(property)
        }

    return createImplClassTypeSpec(
        classData.implName,
        classData,
        implClassProperties,
        classData.functions.map {
            it.toFunSpec(
                resolver,
                ktorfitOptions.setQualifiedType,
                ktorfitOptions.ktorfitLib
            )
        }, ktorfitOptions.ktorfitLib
    )
}

private fun createImplClassTypeSpec(
    implClassName: String,
    classData: ClassData,
    implClassProperties: List<PropertySpec>,
    funSpecs: List<FunSpec>,
    ktorfitLib: Boolean
): TypeSpec {
    val helperProperty =
        PropertySpec
            .builder(converterHelper.objectName, converterHelper.toClassName())
            .initializer(converterHelper.objectName)
            .addModifiers(KModifier.PRIVATE)
            .build()


    val converterProperties = classData.getConverters().map { classDecl ->
        val className = classDecl.toClassName()
        PropertySpec
            .builder(
                className.simpleName.decapitalize(),
                className,
            )
            .initializer("%T()", className)
            .addModifiers(KModifier.PRIVATE)
            .build()
    }


    val constructorBuilder = FunSpec
        .constructorBuilder()
        .addParameter(baseUrl.objectName, String::class)
        .addParameter(httpClient.objectName, httpClient.toClassName())


    if (ktorfitLib) {
        constructorBuilder
            .addParameter(converterHelper.objectName, converterHelper.toClassName())
    }

    val builder = TypeSpec
        .classBuilder(implClassName)
        .addAnnotation(getOptInAnnotation(classData.annotations))
        .addModifiers(classData.modifiers)
        .primaryConstructor(
            constructorBuilder.build()
        ).addProperties(converterProperties)
        .addProperty(
            PropertySpec
                .builder(httpClient.objectName, httpClient.toClassName())
                .initializer(httpClient.objectName)
                .addModifiers(KModifier.PRIVATE)
                .build(),
        ).addProperty(
            PropertySpec
                .builder(baseUrl.objectName, String::class)
                .initializer(baseUrl.objectName)
                .addModifiers(KModifier.PRIVATE)
                .build(),
        ).addProperty(helperProperty)
        .addSuperinterface(ClassName(classData.packageName, classData.name))
        .addKtorfitSuperInterface(classData.superClasses)
        .addProperties(implClassProperties)



    return builder.addFunctions(funSpecs).build()

}

private fun getOptInAnnotation(annotations: List<KSAnnotation>): AnnotationSpec {
    val markerClasses =
        annotations
            .filter { it.shortName.getShortName() == "OptIn" }
            .flatMap { annotation ->
                @Suppress("UNCHECKED_CAST")
                (annotation.arguments[0].value as? List<KSType>)
                    ?.map { it.toClassName() }
                    .orEmpty()
            }
            .plus(internalApi)
            .toTypedArray<Any>()

    val format = (1..markerClasses.size).joinToString { "%T::class" }

    return AnnotationSpec
        .builder(ClassName("kotlin", "OptIn"))
        .addMember(format, *markerClasses)
        .build()
}

private fun propertySpec(property: KSPropertyDeclaration): PropertySpec {
    val propBuilder =
        PropertySpec
            .builder(
                property.simpleName.asString(),
                property.type.toTypeName(),
            ).addModifiers(KModifier.OVERRIDE)
            .mutable(property.isMutable)
            .getter(
                FunSpec
                    .getterBuilder()
                    .addStatement(PROPERTIES_NOT_SUPPORTED)
                    .build(),
            )

    if (property.isMutable) {
        propBuilder.setter(
            FunSpec
                .setterBuilder()
                .addParameter("value", property.type.toTypeName())
                .build(),
        )
    }

    return propBuilder.build()
}

/**
 * Support for extending multiple interfaces, is done with Kotlin delegation.
 * For every know class of [classDataList], there will
 * be a generated implementation for each interface that we can use.
 * @param superClasses List of qualifiedNames of interface that a Ktorfit interface extends
 */
private fun TypeSpec.Builder.addKtorfitSuperInterface(superClasses: List<KSTypeReference>): TypeSpec.Builder {
    (superClasses).forEach { superClassReference ->
        val hasNoDelegationAnnotation =
            superClassReference.annotations.any { it.shortName.getShortName() == "NoDelegation" }

        val superClassDeclaration = superClassReference.resolve().declaration
        val superTypeClassName = superClassDeclaration.simpleName.asString()
        val superTypePackage = superClassDeclaration.packageName.asString()
        if (!hasNoDelegationAnnotation) {
            this.addSuperinterface(
                ClassName(superTypePackage, superTypeClassName),
                CodeBlock.of(
                    "%L._%LImpl(${baseUrl.objectName},${httpClient.objectName},${converterHelper.objectName})",
                    superTypePackage,
                    superTypeClassName,
                )
            )
        }
    }

    return this
}
