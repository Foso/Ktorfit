package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation

fun getCustomRequestBuilderText(parameterDataList: List<ParameterData>): String =
    parameterDataList
        .find { it.hasAnnotation<ParameterAnnotation.RequestBuilder>() }
        ?.let {
            it.name + "(this)"
        }.orEmpty()
