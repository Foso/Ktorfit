package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData

fun getRequestBuilderText(parameterDataList: List<ParameterData>): String {
    return (parameterDataList.find { it.hasRequestBuilderAnno }?.let {
        "requestBuilder = " + it.name
    } ?: "")
}