package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Body

fun getBodyDataText(params: List<ParameterData>): String {
    var bodyText = ""
    params.firstOrNull { it.hasAnnotation<Body>() }?.let {
        bodyText = "bodyData = " + it.name
    }
    return bodyText
}