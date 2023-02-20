package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.RequestBuilder

fun getRequestBuilderText(parameterDataList: List<ParameterData>): String {
    return (parameterDataList.find { it.hasAnnotation<RequestBuilder>() }?.let {
        "requestBuilder = " + it.name
    } ?: "")
}