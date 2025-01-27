package de.jensklingenberg.ktorfit.poetspec

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import de.jensklingenberg.ktorfit.KtorfitOptions
import de.jensklingenberg.ktorfit.model.ClassData
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.PROPERTIES_NOT_SUPPORTED
import de.jensklingenberg.ktorfit.model.converterHelper
import de.jensklingenberg.ktorfit.model.internalApi
import de.jensklingenberg.ktorfit.model.ktorfitClass
import de.jensklingenberg.ktorfit.model.toClassName

/**
 * Transform a [ClassData] to a [FileSpec] for KotlinPoet
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

    val implClassSpec =
        createImplClassTypeSpec(
            classData.implName,
            classData,
            implClassProperties,
            classData.functions.map {
                it.toFunSpec(
                    resolver,
                    ktorfitOptions.setQualifiedType
                )
            }
        )

    return implClassSpec
}

private fun createImplClassTypeSpec(
    implClassName: String,
    classData: ClassData,
    implClassProperties: List<PropertySpec>,
    funSpecs: List<FunSpec>
): TypeSpec {
    val optInAnnotations =
        classData.annotations.filter { it.shortName.getShortName() == "OptIn" }.map { it.toAnnotationSpec() }
    val helperProperty =
        PropertySpec
            .builder(converterHelper.objectName, converterHelper.toClassName())
            .initializer("${converterHelper.name}(${ktorfitClass.objectName})")
            .addModifiers(KModifier.PRIVATE)
            .build()

    val internalApiAnnotation =
        AnnotationSpec
            .builder(ClassName("kotlin", "OptIn"))
            .addMember(
                "%T::class",
                internalApi,
            ).build()
    return TypeSpec
        .classBuilder(implClassName)
        .addAnnotations(optInAnnotations + internalApiAnnotation)
        .addModifiers(classData.modifiers)
        .primaryConstructor(
            FunSpec
                .constructorBuilder()
                .addParameter(ktorfitClass.objectName, ktorfitClass.toClassName())
                .build(),
        ).addProperty(
            PropertySpec
                .builder(ktorfitClass.objectName, ktorfitClass.toClassName())
                .initializer(ktorfitClass.objectName)
                .addModifiers(KModifier.PRIVATE)
                .build(),
        ).addSuperinterface(ClassName(classData.packageName, classData.name))
        .addKtorfitSuperInterface(classData.superClasses)
        .addProperties(listOf(helperProperty) + implClassProperties)
        .addFunctions(funSpecs)
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
                    "%L._%LImpl(${ktorfitClass.objectName})",
                    superTypePackage,
                    superTypeClassName,
                )
            )
        }
    }

    return this
}
