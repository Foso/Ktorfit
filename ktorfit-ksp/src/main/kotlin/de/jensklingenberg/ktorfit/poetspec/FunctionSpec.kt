package de.jensklingenberg.ktorfit.poetspec

import com.google.devtools.ksp.processing.Resolver
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
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
    filePath: String,
): FunSpec {
    val returnTypeName = findTypeName(returnType.parameterType, filePath)

    return FunSpec
        .builder(this.name)
        .addModifiers(
            mutableListOf(KModifier.OVERRIDE).also {
                if (this.isSuspend) {
                    it.add(KModifier.SUSPEND)
                }
            },
        ).returns(returnTypeName)
        .addParameters(
            parameterDataList.map {
                it.parameterSpec(filePath)
            },
        ).addBody(this, resolver, setQualifiedTypeName, returnTypeName)
        .build()
}

private fun FunSpec.Builder.addBody(
    functionData: FunctionData,
    resolver: Resolver,
    setQualifiedTypeName: Boolean,
    returnTypeName: TypeName
) = apply {
    addRequestConverterText(functionData.parameterDataList)
        .addStatement(
            getReqBuilderExtensionText(
                functionData,
                resolver,
            ),
        ).addStatement(
            "val ${typeDataClass.objectName} = ${typeDataClass.name}.createTypeData(",
        ).addStatement("typeInfo = typeInfo<%T>(),", returnTypeName)
        .addStatement(
            if (setQualifiedTypeName) {
                "qualifiedTypename = \"${
                    returnTypeName.toString().removeWhiteSpaces()
                }\")"
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
            if (functionData.returnType.parameterType.isMarkedNullable) {
                ""
            } else {
                "!!"
            },
        )
}
