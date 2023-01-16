package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toKModifier
import de.jensklingenberg.ktorfit.utils.addImports
import de.jensklingenberg.ktorfit.utils.getFileImports
import de.jensklingenberg.ktorfit.utils.resolveTypeName

/**
 * @param name of the interface that contains annotations
 * @param superClasses List of qualifiedNames of interface that a Ktorfit interface extends
 */
data class ClassData(
    val name: String,
    val packageName: String,
    val functions: List<FunctionData>,
    val imports: List<String>,
    val superClasses: List<String> = emptyList(),
    val properties: List<KSPropertyDeclaration> = emptyList(),
    val modifiers: List<KModifier> = emptyList()
)

const val WILDCARDIMPORT = "WILDCARDIMPORT"

/**
 * Transform a [ClassData] to a [FileSpec] for KotlinPoet
 */
fun ClassData.getImplClassFileSource(): String {
    val classData = this
classData.functions.first()
    /**
     * public fun Ktorfit.createExampleApi(): ExampleApi = _ExampleApiImpl(KtorfitClient(this))
     * _JsonPlaceHolderApiImpl(KtorfitClient(this)).also { it.setClient(KtorfitClient(this)) }
     */
    val createExtensionFunctionSpec = FunSpec.builder("create${classData.name}")
        .addModifiers(classData.modifiers)
        .addStatement("return _${classData.name}Impl().also{ it.setClient(KtorfitClient(this)) }")
        .receiver(TypeVariableName(ktorfitClass.name))
        .returns(TypeVariableName(classData.name))
        .build()

    val func = FunSpec.builder("setClient")
        .addModifiers(KModifier.OVERRIDE)
        .addParameter("client", TypeVariableName(clientClass.name))
        .addStatement("this.client = client")
        .build()

    val properties = classData.properties.map { property ->
        val propBuilder = PropertySpec.builder(
            property.simpleName.asString(),
            TypeVariableName(property.type.resolve().resolveTypeName())
        )
            .addModifiers(KModifier.OVERRIDE)
            .mutable(property.isMutable)
            .getter(
                FunSpec.getterBuilder()
                    .addStatement("TODO(\"Not yet implemented\")")
                    .build()
            )

        if (property.isMutable) {
            propBuilder.setter(
                FunSpec.setterBuilder()
                    .addParameter("value", TypeVariableName(property.type.resolve().resolveTypeName()))
                    .build()
            )
        }

        propBuilder.build()
    }

    val implClassName = "_${classData.name}Impl"

    return FileSpec.builder(classData.packageName, implClassName)
        .addFileComment("Generated by Ktorfit")
        .addImports(classData.imports)
        .addType(
            TypeSpec.classBuilder(implClassName)
                .addModifiers(classData.modifiers)
                .addSuperinterface(ClassName(classData.packageName, classData.name))
                .addSuperinterface(ClassName("de.jensklingenberg.ktorfit", "KtorfitService"))
                .addKtorfitSuperInterface(classData.superClasses)

                .addFunctions(classData.functions.map { it.toFunSpec() }.flatten())
                .addFunction(func)
                .addProperty(
                    PropertySpec
                        .builder(
                            "client",
                            TypeVariableName(clientClass.name),
                            listOf(KModifier.PRIVATE, KModifier.LATEINIT)
                        )
                        .mutable(true)
                        .build()
                )
                .addProperties(properties)
                .build()
        )
        .addFunction(createExtensionFunctionSpec)

        .build().toString().replace(WILDCARDIMPORT, "*")
}


/**
 * Convert a [KSClassDeclaration] to [ClassData]
 * @param ksClassDeclaration interface that contains Ktorfit annotations
 * @param logger used to log errors
 * @return the transformed classdata
 */
fun KSClassDeclaration.toClassData(logger: KSPLogger): ClassData {
    val ksClassDeclaration = this
    val imports = ksClassDeclaration.getFileImports().toMutableList()
    val packageName = ksClassDeclaration.packageName.asString()
    val className = ksClassDeclaration.simpleName.asString()

    val isJavaClass = ksClassDeclaration.origin.name == "JAVA"
    if (isJavaClass) {
        logger.error(KtorfitError.JAVA_INTERFACES_ARE_NOT_SUPPORTED, ksClassDeclaration)
    }

    val isInterface = ksClassDeclaration.classKind == ClassKind.INTERFACE
    if (!isInterface) {
        logger.error(KtorfitError.API_DECLARATIONS_MUST_BE_INTERFACES, ksClassDeclaration)
    }

    val hasTypeParameters = ksClassDeclaration.typeParameters.isNotEmpty()
    if (hasTypeParameters) {
        logger.error(
            KtorfitError.TYPE_PARAMETERS_ARE_UNSUPPORTED_ON + " ${ksClassDeclaration.simpleName.asString()}",
            ksClassDeclaration
        )
    }

    val functionDataList: List<FunctionData> =
        ksClassDeclaration.getDeclaredFunctions().toList().map { funcDeclaration ->
            return@map funcDeclaration.toFunctionData(logger, imports, packageName)
        }


    if (functionDataList.any { it.parameterDataList.any { param -> param.hasRequestTypeAnnotation() } }) {
        imports.add("kotlin.reflect.cast")
    }

    val supertypes =
        ksClassDeclaration.superTypes.toList().filterNot {
            /** In KSP Any is a supertype of an interface */
            it.resolve().resolveTypeName() == "Any"
        }.mapNotNull { it.resolve().declaration.qualifiedName?.asString() }
    val properties = ksClassDeclaration.getAllProperties().toList()

    if (packageName.isEmpty()) {
        logger.error(KtorfitError.INTERFACE_NEEDS_TO_HAVE_A_PACKAGE, ksClassDeclaration)
    }

    return ClassData(
        name = className,
        packageName = packageName,
        functions = functionDataList,
        imports = imports,
        superClasses = supertypes,
        properties = properties,
        modifiers = ksClassDeclaration.modifiers.mapNotNull { it.toKModifier() })
}

/**
 * Support for extending multiple interfaces, is done with Kotlin delegation. Ktorfit interfaces can only extend other Ktorfit interfaces, so there will
 * be a generated implementation for each interface that we can use.
 */
fun TypeSpec.Builder.addKtorfitSuperInterface(superClasses: List<String>): TypeSpec.Builder {
    (superClasses).forEach { superClassQualifiedName ->
        val superTypeClassName = superClassQualifiedName.substringAfterLast(".")
        val superTypePackage = superClassQualifiedName.substringBeforeLast(".")
        this.addSuperinterface(
            ClassName(superTypePackage, superTypeClassName),
            CodeBlock.of("${superTypePackage}._${superTypeClassName}Impl()")
        )
    }

    return this
}

