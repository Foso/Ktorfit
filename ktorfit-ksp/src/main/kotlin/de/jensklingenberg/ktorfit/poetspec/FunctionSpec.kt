package de.jensklingenberg.ktorfit.poetspec

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.processing.Resolver
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.converterHelper
import de.jensklingenberg.ktorfit.model.extDataClass
import de.jensklingenberg.ktorfit.model.typeDataClass
import de.jensklingenberg.ktorfit.reqBuilderExtension.addRequestConverterText
import de.jensklingenberg.ktorfit.reqBuilderExtension.getReqBuilderExtensionText
import de.jensklingenberg.ktorfit.utils.removeWhiteSpaces

fun FunctionData.toFunSpec(
    resolver: Resolver,
    setQualifiedTypeName: Boolean,
): FunSpec {
    val returnTypeName = returnType.typeName ?: throw IllegalStateException("Return type not found")

    return FunSpec
        .builder(name)
        .addModifiers(modifiers)
        .addAnnotations(rawOptInAnnotations)
        .addParameters(
            parameterDataList.map {
                it.parameterSpec()
            },
        )
        .addBody(this, resolver, setQualifiedTypeName, returnTypeName)
        .returns(returnTypeName)
        .build()
}

@OptIn(KspExperimental::class)
private fun FunSpec.Builder.addBody(
    functionData: FunctionData,
    resolver: Resolver,
    setQualifiedTypeName: Boolean,
    returnTypeName: TypeName
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
