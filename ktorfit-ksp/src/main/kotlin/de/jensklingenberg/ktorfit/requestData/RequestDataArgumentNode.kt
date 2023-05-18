package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.requestDataClass


fun getRequestDataArgumentText(functionData: FunctionData): String {

    //URL
    val requestTypeInfoText = getRequestTypeInfoText(functionData.returnType)
    val qualifiedTypeName = "returnTypeData = ${functionData.returnType.qualifiedName}"
    val ktorfitRequestBuilderText = "ktorfitRequestBuilder = _ext"
    val args = listOf(
        qualifiedTypeName,
        requestTypeInfoText,
        ktorfitRequestBuilderText
    ).filter { it.isNotEmpty() }.joinToString(",\n") { it }

    return "val _requestData = ${requestDataClass.name}($args) \n"
}
