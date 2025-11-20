package de.jensklingenberg.ktorfit.poetspec

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.converterHelper
import de.jensklingenberg.ktorfit.model.extDataClass
import de.jensklingenberg.ktorfit.model.typeDataClass
import de.jensklingenberg.ktorfit.reqBuilderExtension.addRequestConverterText
import de.jensklingenberg.ktorfit.reqBuilderExtension.getReqBuilderExtensionText
import de.jensklingenberg.ktorfit.utils.isSuspend
import de.jensklingenberg.ktorfit.utils.removeWhiteSpaces

fun FunctionData.toFunSpec(
    resolver: Resolver,
    setQualifiedTypeName: Boolean,
    converters: List<KSClassDeclaration>,
): FunSpec {
    val returnTypeName = returnType.typeName ?: throw IllegalStateException("Return type not found")

    return FunSpec
        .builder(name)
        .addModifiers(modifiers)
        .addAnnotations(optInAnnotations)
        .addParameters(
            parameterDataList.map {
                it.parameterSpec()
            },
        ).addBody(this, resolver, setQualifiedTypeName, returnTypeName, converters)
        .returns(returnTypeName)
        .build()
}

@OptIn(KspExperimental::class)
private fun FunSpec.Builder.addBody(
    functionData: FunctionData,
    resolver: Resolver,
    setQualifiedTypeName: Boolean,
    returnTypeName: TypeName,
    converters: List<KSClassDeclaration>
) = apply {
    val listType =
        resolver.getKotlinClassByName("kotlin.collections.List")?.asStarProjectedType() ?: error("List not found")


    val arrayType = resolver.builtIns.arrayType.starProjection()
    addRequestConverterText(functionData.parameterDataList)
        .addStatement(
            getReqBuilderExtensionText(
                functionData,
                listType,
                arrayType,
            ),
        ).addStatement(
            if (functionData.isSuspend) {
                val responseTypeName = ClassName("io.ktor.client.statemen", "HttpResponse")
                val e = converters
                    .map { it.getDeclaredFunctions().toList() }
                    .flatten()
                    .firstOrNull {
                        it.parameters.firstOrNull()?.type.toString().contains("HttpResponse") &&
                                it.returnType?.toTypeName() == functionData.returnType.typeName &&
                                it.isSuspend
                    }


                val convFunction = e?.simpleName?.asString()
                val parentName = (e?.parent as? KSClassDeclaration)?.simpleName?.asString()?.replaceFirstChar { it.lowercase() }
                if (e?.returnType?.toTypeName() == functionData.returnType.typeName) {
                    "val _response = _ktorfit.httpClient.request(_ext) \n" +
                            "return $parentName.$convFunction(_response)"

                } else {
                    "val _response = _ktorfit.httpClient.request(_ext)"
                }


            } else {
                val e = converters
                    .map { it.getDeclaredFunctions().toList() }
                    .flatten()
                    .firstOrNull {
                        it.parameters.firstOrNull()?.type.toString().contains("SuspendFunction") &&
                                it.returnType?.toTypeName() == functionData.returnType.typeName &&
                                !it.isSuspend
                    }

                val convFunction = e?.simpleName?.asString()
                val parentName = (e?.parent as? KSClassDeclaration)?.simpleName?.asString()?.replaceFirstChar { it.lowercase() }
                if(convFunction !=null && parentName!=null)
                 "return $parentName.$convFunction{_ktorfit.httpClient.request(_ext)}"
                else{
                    ""
                }
            })
        .addStatement(
            "val ${typeDataClass.objectName} = ${typeDataClass.name}.createTypeData("
        ).addStatement("typeInfo = typeInfo<%T>(),", returnTypeName)
        .addStatement(
            if (setQualifiedTypeName) {
                buildString {
                    append("qualifiedTypename = \"")
                    append(returnTypeName.toString().removeWhiteSpaces())
                    append("\")")
                }
            } else {
                ")"
            },
        ).addStatement(
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
