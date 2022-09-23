package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.Url

/**
 * Source for the "relativeUrl" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getRelativeUrlArgumentText(methodAnnotation: HttpMethodAnnotation, params: List<ParameterData>): String {

    var urlPath = ""

    if (methodAnnotation.path.isNotEmpty()) {
        urlPath = methodAnnotation.path
    } else {
        params.firstOrNull { it.hasAnnotation<Url>() }?.let {
            urlPath = "\${" + it.name + "}"
        }
    }

    return "relativeUrl=\"$urlPath\""
}

