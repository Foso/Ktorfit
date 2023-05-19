package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.requestDataClass
import de.jensklingenberg.ktorfit.utils.removeWhiteSpaces


fun getRequestDataArgumentText(functionData: FunctionData): String {

    val ktorfitRequestBuilderText = "ktorfitRequestBuilder = _ext"
    val returnTypeName = "returnTypeName = \"${functionData.returnType.qualifiedName.removeWhiteSpaces()}\""
    val returnTypeInfo = getReturnTypeInfoText(functionData.returnType.name)
    val args = listOf(
        ktorfitRequestBuilderText,
        returnTypeName,
        returnTypeInfo
    ).filter { it.isNotEmpty() }.joinToString(",\n") { it }

    return "val _requestData = ${requestDataClass.name}($args) \n"
}
