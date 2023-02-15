package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.requestDataClass
import de.jensklingenberg.ktorfit.model.FunctionData


fun getRequestDataArgumentText(functionData: FunctionData): String {
    val methodAnnotation = functionData.httpMethodAnnotation
    //METHOD
    val method = "method=\"${methodAnnotation.httpMethod.keyword}\""
    //HEADERS
    val headersText = getHeadersArgumentText(functionData.annotations, functionData.parameterDataList)
    //BODY
    val body = getBodyDataText(functionData.parameterDataList)
    //URL
    val urlPath = getRelativeUrlArgumentText(functionData.httpMethodAnnotation, functionData.parameterDataList)
    val pathsText = getPathsText(functionData.parameterDataList)
    val queryText = getQueryArgumentText(functionData.parameterDataList)
    val fieldsText = getFieldArgumentsText(functionData.parameterDataList)
    val partsText = getPartsArgumentText(functionData.parameterDataList)
    val builderText = getRequestBuilderText(functionData.parameterDataList)
    val requestTypeInfoText = getRequestTypeInfoText(functionData)
    val returnTypeInfoText = getReturnTypeInfoText(functionData)
    val qualifiedTypeName = "returnTypeData = ${functionData.returnType.qualifiedName}"

    val args = listOf(
        method,
        urlPath,
        headersText,
        body,
        queryText,
        fieldsText,
        partsText,
        builderText,
        qualifiedTypeName,
        pathsText,
        requestTypeInfoText,
        returnTypeInfoText
    ).filter { it.isNotEmpty() }.joinToString(",\n") { it }

    return "val requestData = ${requestDataClass.name}($args) \n"
}
