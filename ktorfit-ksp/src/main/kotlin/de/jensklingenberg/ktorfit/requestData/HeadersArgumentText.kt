package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.*
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.*


/**
 * Source for the "headers" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getHeadersArgumentText(
    functionAnnotations: List<FunctionAnnotation>,
    paramList: List<ParameterData>
): String {
    val headerList = mutableListOf<Pair<String, String>>()

    val paramsWithHeaderAnno = paramList.filter { it.hasAnnotation<Header>() }
    val headersAnno = functionAnnotations.filterIsInstance<Headers>().firstOrNull()
    val paramsWithHeaderMap = paramList.filter { it.hasAnnotation<HeaderMap>() }

    if (functionAnnotations.any { it is FormUrlEncoded }) {
        /**
         * Can't add Content Type Header, because it leads to Ktor issues https://github.com/ktorio/ktor/issues/1127
         */
        //  headerList.add(Pair("\"Content-Type\"", "\"application/x-www-form-urlencoded\""))
    }

    paramsWithHeaderAnno.forEach { myParam ->
        val paramName = myParam.name
        val headerPath = myParam.findAnnotationOrNull<Header>()?.path ?: ""

        headerList.add(Pair("\"${headerPath}\"", paramName))
    }

    headersAnno?.let { headers ->
        headers.path.forEach {
            val (key, value) = it.split(":")

            headerList.add(Pair("\"" + key + "\"", "\"" + value + "\""))
        }
    }

    paramsWithHeaderMap.forEach {
        headerList.add(Pair("\"\"", it.name))
    }


    val headerText = headerList.joinToString {
        val (key, value) = it

        "HeaderData($key,$value)"
    }.surroundIfNotEmpty("headers = listOf(", ")")


    return headerText.replace(" ", "·")
}