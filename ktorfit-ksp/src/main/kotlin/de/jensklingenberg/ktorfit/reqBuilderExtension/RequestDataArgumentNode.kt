package de.jensklingenberg.ktorfit.reqBuilderExtension


import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Body
import de.jensklingenberg.ktorfit.model.annotations.CustomHttp


fun getReqBuilderExtensionText(functionData: FunctionData): String {
    val methodAnnotation = functionData.httpMethodAnnotation
    //METHOD
    val httpMethodValue = if(methodAnnotation is CustomHttp){
        methodAnnotation.customValue
    }else{
        methodAnnotation.httpMethod.keyword
    }
    val method = "this.method = HttpMethod.parse(\"${httpMethodValue}\")"
    val body = getBodyDataText(functionData.parameterDataList)
    val args = listOf(
        method,
        body
    ).filter { it.isNotEmpty() }.joinToString("\n") { it }

    return "val _ext: HttpRequestBuilder.() -> Unit = {\n$args \n}"
}

fun getBodyDataText(params: List<ParameterData>): String {
    return params.firstOrNull { it.hasAnnotation<Body>() }?.let {
        "setBody(" + it.name + ")"
    } ?: ""
}