package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.RequestBuilder

fun getCustomRequestBuilderText(parameterDataList: List<ParameterData>): String {
    return (parameterDataList.find { it.hasAnnotation<RequestBuilder>() }?.let {
        it.name + "(this)"
    } ?: "")
}