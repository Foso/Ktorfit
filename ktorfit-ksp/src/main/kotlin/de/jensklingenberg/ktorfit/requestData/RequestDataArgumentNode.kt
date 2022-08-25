package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.generator.requestDataClass
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Body


private fun getRequestBuilderText(parameterDataList: List<ParameterData>): String {
    return (parameterDataList.find { it.hasRequestBuilderAnno }?.let {
        "requestBuilder = " + it.name
    } ?: "")
}

private fun getBodyDataText(params: List<ParameterData>): String {
    var bodyText = ""
    params.firstOrNull { it.hasAnnotation<Body>() }?.let {
        bodyText = "bodyData = " + it.name
    }
    return bodyText
}

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
    val queryText = getQueryArgumentText(functionData.parameterDataList)
    val fieldsText = getFieldArgumentsText(functionData.parameterDataList)
    val partsText = getPartsArgumentText(functionData.parameterDataList)
    val builderText = getRequestBuilderText(functionData.parameterDataList)

    val qualifiedTypeName = "qualifiedRawTypeName=\"${functionData.returnType.qualifiedName}\""

    val args = listOf(
        method,
        urlPath,
        headersText,
        body,
        queryText,
        fieldsText,
        partsText,
        builderText,
        qualifiedTypeName
    ).filter { it.isNotEmpty() }.joinToString(",\n") { it }

    return "val requestData = ${requestDataClass.name}($args) \n"
}
