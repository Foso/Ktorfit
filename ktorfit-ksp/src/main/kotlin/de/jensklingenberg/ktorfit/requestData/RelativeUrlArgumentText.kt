package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.findAnnotationOrNull
import de.jensklingenberg.ktorfit.hasAnnotation
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.Path
import de.jensklingenberg.ktorfit.model.annotations.Url

/**
 * Source for the "relativeUrl" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getRelativeUrlArgumentText(methodAnnotation: HttpMethodAnnotation, params: List<ParameterData>): String {

    var urlPath = ""

    if (methodAnnotation.path.isNotEmpty()) {
        //url="posts"
        urlPath = methodAnnotation.path
    } else {
        params.firstOrNull { it.hasAnnotation<Url>() }?.let {
            //url=$foo
            urlPath = "\${" + it.name + "}"
        }
    }

    /**
     * Replace all values with curly braces in url path to corresponding annotated parameter names
     */
    params.filter { it.hasAnnotation<Path>() }.forEach { myParam ->
        val paramName = myParam.name
        val pathAnnotation = myParam.findAnnotationOrNull<Path>()
        val pathPath = pathAnnotation?.value ?: ""
        val pathEncoded = pathAnnotation?.encoded ?: false

        val newPathValue = if (!pathEncoded) {
            "\${client.encode($paramName)}"
        } else {
            "\${$paramName}"
        }

        urlPath = urlPath.replace("{${pathPath}}", newPathValue)
    }


    return "relativeUrl=\"$urlPath\""
}