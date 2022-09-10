package de.jensklingenberg.ktorfit.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.*
import de.jensklingenberg.ktorfit.model.ClassData
import de.jensklingenberg.ktorfit.requestData.getRequestDataArgumentText
import de.jensklingenberg.ktorfit.utils.resolveTypeName
import java.io.OutputStreamWriter

private const val WILDCARDIMPORT = "WILDCARDIMPORT"

/**
 * Generate the Impl class for every interface used for Ktorfit
 */
fun generateImplClass(classDataList: List<ClassData>, codeGenerator: CodeGenerator) {
    classDataList.forEach { classData ->
        val fileSource = getFileSpec(classData).toString().replace(WILDCARDIMPORT, "*")

        val packageName = classData.packageName
        val className = classData.name

        codeGenerator.createNewFile(Dependencies.ALL_FILES, packageName, "_${className}Impl", "kt").use { output ->
            OutputStreamWriter(output).use { writer ->
                writer.write(fileSource)
            }
        }
    }
}

/**
 * Transform a [ClassData] to a [FileSpec] for KotlinPoet
 */
fun getFileSpec(classData: ClassData): FileSpec {

    val createFunctionSpec = FunSpec.builder("create${classData.name}")
        .addStatement("return _${classData.name}Impl(${clientClass.name}(this))")
        .receiver(TypeVariableName(ktorfitClass.name))
        .returns(TypeVariableName(classData.name))
        .build()

    val funSpecList: List<FunSpec> = getFunSpecs(classData)

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
        .addImport(clientClass.packageName, clientClass.name)
        .addType(
            TypeSpec.classBuilder(implClassName)
                .addModifiers(classData.modifiers)
                .addSuperinterface(ClassName(classData.packageName, classData.name))
                .addKtorfitSuperInterface(classData.superClasses)
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter(clientClass.objectName, TypeVariableName(clientClass.name))
                        .build()
                )
                .addProperty(
                    PropertySpec.builder(clientClass.objectName, TypeVariableName(clientClass.name))
                        .addModifiers(KModifier.PRIVATE)
                        .initializer(clientClass.objectName)
                        .build()
                )
                .addFunctions(funSpecList)
                .addProperties(properties)
                .build()
        )
        .addFunction(createFunctionSpec)

        .build()
}


fun getFunSpecs(classData: ClassData): List<FunSpec> = classData.functions.map { functionData ->

    val returnTypeName = functionData.returnType.name
    val typeWithoutOuterType = returnTypeName.substringAfter("<").substringBeforeLast(">")

    FunSpec.builder(functionData.name)
        .addModifiers(mutableListOf(KModifier.OVERRIDE).also {
            if (functionData.isSuspend) {
                it.add(KModifier.SUSPEND)
            }
        })
        .returns(TypeVariableName(functionData.returnType.name))
        .addParameters(functionData.parameterDataList.map {
            ParameterSpec(it.name, TypeVariableName(it.type.name))
        })
        .addStatement(
            getRequestDataArgumentText(
                functionData,
            )
        )
        .addStatement(
            if (functionData.isSuspend) {
                "return ${clientClass.objectName}.suspendRequest<${returnTypeName}, $typeWithoutOuterType>(${requestDataClass.objectName})"
            } else {
                "return ${clientClass.objectName}.request<${returnTypeName}, $typeWithoutOuterType>(${requestDataClass.objectName})"
            }
        )
        .build()
}

/**
 * Support for extending multiple interfaces, is done with Kotlin delegation. Ktorfit interfaces can only extend other Ktorfit interfaces, so there will
 * be a generated implementation for each interface that we can use.
 */
fun TypeSpec.Builder.addKtorfitSuperInterface(superClasses: List<String>): TypeSpec.Builder {
    superClasses.forEach { superClassQualifiedName ->
        val superTypeClassName = superClassQualifiedName.substringAfterLast(".")
        val superTypePackage = superClassQualifiedName.substringBeforeLast(".")
        this.addSuperinterface(
            ClassName(superTypePackage, superTypeClassName),
            CodeBlock.of("${superTypePackage}._${superTypeClassName}Impl(${clientClass.objectName})")
        )
    }

    return this
}


fun FileSpec.Builder.addImports(imports: List<String>): FileSpec.Builder {

    imports.forEach {
        /**
         * Wildcard imports are not allowed by KotlinPoet, as a workaround * is replaced with WILDCARDIMPORT, and it will be replaced again
         * after Kotlin Poet generated the source code
         */
        val packageName = it.substringBeforeLast(".")
        val className = it.substringAfterLast(".").replace("*", WILDCARDIMPORT)

        this.addImport(packageName, className)
    }
    return this
}