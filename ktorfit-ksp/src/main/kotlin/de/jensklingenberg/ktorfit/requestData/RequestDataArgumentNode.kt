package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.generator.requestDataClass
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

    val nullable = if(functionData.returnType.name.endsWith("?")){
        "?"
    }else{
        ""
    }
    val qualifiedTypeName = "qualifiedRawTypeName=\"${functionData.returnType.qualifiedName}$nullable\""

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
        pathsText
    ).filter { it.isNotEmpty() }.joinToString(",\n") { it }

    return "val requestData = ${requestDataClass.name}($args) \n"
}
