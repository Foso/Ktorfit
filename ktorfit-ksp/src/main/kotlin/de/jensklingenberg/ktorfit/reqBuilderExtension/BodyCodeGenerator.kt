package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Body

fun getBodyDataText(params: List<ParameterData>): String {
    return params.firstOrNull { it.hasAnnotation<Body>() }?.let {
        "setBody(%s)".format(it.name)
    } ?: ""
}
