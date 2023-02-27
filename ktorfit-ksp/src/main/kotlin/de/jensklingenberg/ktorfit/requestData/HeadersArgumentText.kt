package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.*
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.*
import de.jensklingenberg.ktorfit.utils.anyInstance
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty
import de.jensklingenberg.ktorfit.utils.surroundWith


/**
 * Source for the "headers" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getHeadersArgumentText(
    functionAnnotations: List<FunctionAnnotation>,
    paramList: List<ParameterData>
): String {
    val headerList = mutableListOf<Pair<String, String>>()

    if (functionAnnotations.anyInstance<FormUrlEncoded>()) {
        val contentTypeHeader = "\"Content-Type\"" to "\"application/x-www-form-urlencoded\""
        headerList.add(contentTypeHeader)
    }

    val parameterWithHeaderAnnotationList = paramList.filter { it.hasAnnotation<Header>() }

    parameterWithHeaderAnnotationList.forEach { myParam ->
        val paramName = myParam.name
        val headerPath = myParam.findAnnotationOrNull<Header>()?.path ?: ""

        headerList.add("\"${headerPath}\"" to paramName)
    }

    val headersAnno = functionAnnotations.filterIsInstance<Headers>().firstOrNull()

    headersAnno?.path?.forEach {
        val (key, value) = it.split(":")
        headerList.add(Pair(key.surroundWith("\""), value.trim().surroundWith("\"")))
    }

    val paramsWithHeaderMap = paramList.filter { it.hasAnnotation<HeaderMap>() }

    paramsWithHeaderMap.forEach {
        headerList.add("\"\"" to it.name)
    }


    val headerText = headerList.joinToString {
        val (key, value) = it

        "DH($key,$value)"
    }.surroundIfNotEmpty("headers = listOf(", ")").replace(" ", "·")


    return headerText
}