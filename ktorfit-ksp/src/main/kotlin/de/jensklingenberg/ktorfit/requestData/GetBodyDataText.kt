package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Body

fun getBodyDataText(params: List<ParameterData>): String {
    return params.firstOrNull { it.hasAnnotation<Body>() }?.let {
        "bodyData = " + it.name
    } ?: ""
}