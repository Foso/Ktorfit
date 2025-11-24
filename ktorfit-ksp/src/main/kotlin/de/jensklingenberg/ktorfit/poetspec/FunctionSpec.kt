package de.jensklingenberg.ktorfit.poetspec

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.converterHelper
import de.jensklingenberg.ktorfit.model.extDataClass
import de.jensklingenberg.ktorfit.model.httpClient
import de.jensklingenberg.ktorfit.model.toClassName
import de.jensklingenberg.ktorfit.model.typeDataClass
import de.jensklingenberg.ktorfit.model.typeInfoClass
import de.jensklingenberg.ktorfit.reqBuilderExtension.addRequestConverterText
import de.jensklingenberg.ktorfit.reqBuilderExtension.getReqBuilderExtensionText
import de.jensklingenberg.ktorfit.utils.removeWhiteSpaces
import java.lang.Exception

@OptIn(KspExperimental::class)
fun FunctionData.toFunSpec(
    resolver: Resolver,
    setQualifiedTypeName: Boolean,
    ktorfitLib: Boolean,
): FunSpec {
    val returnTypeName = returnType.typeName ?: throw IllegalStateException("Return type not found")

    val parameterSpecs = parameterDataList.map {
        it.parameterSpec()
    }

    val listType =
        resolver.getKotlinClassByName("kotlin.collections.List")?.asStarProjectedType() ?: error("List not found")

    val arrayType = resolver.builtIns.arrayType.starProjection()

    return FunSpec
        .builder(name)
        .addModifiers(modifiers)
        .addAnnotations(optInAnnotations)
        .addParameters(parameterSpecs)
        .addBody(this, setQualifiedTypeName, returnTypeName, ktorfitLib, listType, arrayType)
        .returns(returnTypeName)
        .build()
}

private fun FunSpec.Builder.addBody(
    functionData: FunctionData,
    setQualifiedTypeName: Boolean,
    returnTypeName: TypeName,
    ktorfitLib: Boolean,
    listType: KSType,
    arrayType: KSType
) = apply {
    val matchingConverterFunctions = functionData.matchingConverterFunction
    val convFunction = matchingConverterFunctions?.simpleName?.asString()
    val parentName = (matchingConverterFunctions?.parent as? KSClassDeclaration)?.simpleName?.asString()?.replaceFirstChar { it.lowercase() }

    val star = if (functionData.isSuspend) {

        if (matchingConverterFunctions?.returnType?.toTypeName() == functionData.returnType.typeName) {

            val arguments = matchingConverterFunctions?.parameters?.joinToString(prefix = "{") {
                try {
                    if (it.type.toTypeName().toString() == "suspend () -> io.ktor.client.statement.HttpResponse") {
                        "${httpClient.objectName}.request(_ext)}"
                    } else if (it.type.toTypeName().toString() == "io.ktor.util.reflect.TypeInfo") {
                        "\ntypeInfo<$returnTypeName>()"
                    } else if (it.type.toTypeName().toString() == "io.ktor.client.HttpClient") {
                        "\n${httpClient.objectName}"
                    } else {
                        throw IllegalStateException("Unknown parameter type ${it.type.toTypeName()} for function $convFunction Only suspend () -> HttpResponse, TypeInfo and HttpClient are supported")
                    }
                } catch (ex: Exception) {
                    throw IllegalStateException("Unknown parameter type ${it.type.toTypeName()} for function $convFunction Only suspend () -> HttpResponse, TypeInfo and HttpClient are supported")
                }
            }

            val typeArguments = if (matchingConverterFunctions!!.typeParameters.isNotEmpty()) {
                "<${matchingConverterFunctions.typeParameters.joinToString(",") { functionData.returnType.name }}>"
            } else {
                ""
            }

            "return $parentName.$convFunction$typeArguments($arguments)"

        } else {
            if (ktorfitLib) {
                ""
            } else {
                "return ${httpClient.objectName}.request(_ext).body() as $returnTypeName"
            }
        }

    } else {

        if (convFunction != null && parentName != null) {

            val arguments = matchingConverterFunctions.parameters.joinToString(prefix = "{") {
                try {
                    if (it.type.toTypeName().toString() == "suspend () -> io.ktor.client.statement.HttpResponse") {
                        "${httpClient.objectName}.request(_ext)}"
                    } else if (it.type.toTypeName().toString() == "io.ktor.util.reflect.TypeInfo") {
                        "\ntypeInfo<$returnTypeName>()"
                    } else if (it.type.toTypeName().toString() == "io.ktor.client.HttpClient") {
                        "\n${httpClient.objectName}"
                    } else {
                        throw IllegalStateException("Unknown parameter type ${it.type.toTypeName()} for function $convFunction Only suspend () -> HttpResponse, TypeInfo and HttpClient are supported")
                    }
                } catch (ex: Exception) {
                    throw IllegalStateException("Unknown parameter type ${it.type.toTypeName()} for function $convFunction Only suspend () -> HttpResponse, TypeInfo and HttpClient are supported")
                }
            }

            val typeArguments = if (matchingConverterFunctions.typeParameters.isNotEmpty()) {
                matchingConverterFunctions.typeParameters.joinToString(prefix = "<", separator = ",", postfix = ">") { functionData.returnType.name }
            } else {
                ""
            }
            "return $parentName.$convFunction$typeArguments($arguments) as $returnTypeName"
        } else {
            ""
        }
    }

    val builder = addRequestConverterText(functionData.parameterDataList)
    builder.addStatement(
        getReqBuilderExtensionText(
            functionData,
            listType,
            arrayType,
        ),
    )
    if (ktorfitLib) {
        builder.addStatement("val %N = %T.createTypeData(", typeDataClass.objectName, typeDataClass.toClassName())
            .addStatement("%T = typeInfo<%T>())", typeInfoClass.toClassName(), returnTypeName)
    }
    builder.addStatement(
        star
    )

    if (ktorfitLib) {
        builder.addStatement(
            if (setQualifiedTypeName) {
                buildString {
                    append("qualifiedTypename = \"")
                    append(returnTypeName.toString().removeWhiteSpaces())
                    append("\")")
                }
            } else {
                ""
            },
        )

        builder.addStatement(
            "return %L.%L(%L,${extDataClass.objectName})%L",
            converterHelper.objectName,
            if (functionData.isSuspend) {
                "suspendRequest"
            } else {
                "request"
            },
            typeDataClass.objectName,
            "!!".takeIf { !functionData.returnType.parameterType.isMarkedNullable }.orEmpty(),
        )
    }
}
