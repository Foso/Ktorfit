package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.requestDataClass
import de.jensklingenberg.ktorfit.model.FunctionData


fun getRequestDataArgumentText(functionData: FunctionData): String {

    //URL
    val urlPath = getRelativeUrlArgumentText(functionData.httpMethodAnnotation, functionData.parameterDataList)
    val pathsText = getPathsText(functionData.parameterDataList)
    val queryText = getQueryArgumentText(functionData.parameterDataList)
    val fieldsText = getFieldArgumentsText(functionData.parameterDataList)
    val partsText = getPartsArgumentText(functionData.parameterDataList)
    val builderText = getRequestBuilderText(functionData.parameterDataList)
    val requestTypeInfoText = getRequestTypeInfoText(functionData.returnType)
    val returnTypeInfoText = getReturnTypeInfoText(functionData.returnType)
    val qualifiedTypeName = "returnTypeData = ${functionData.returnType.qualifiedName}"
    val ktorfitRequestBuilderText = "ktorfitRequestBuilder = _ext"
    val args = listOf(
        urlPath,
        queryText,
        fieldsText,
        partsText,
        builderText,
        qualifiedTypeName,
        pathsText,
        requestTypeInfoText,
        returnTypeInfoText,
        ktorfitRequestBuilderText
    ).filter { it.isNotEmpty() }.joinToString(",\n") { it }

    return "val requestData = ${requestDataClass.name}($args) \n"
}
