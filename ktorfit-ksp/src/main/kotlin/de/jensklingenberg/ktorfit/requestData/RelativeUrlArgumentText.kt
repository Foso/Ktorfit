package de.jensklingenberg.ktorfit.requestData


import de.jensklingenberg.ktorfit.generator.pathDataClass
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

    return "relativeUrl=\"$urlPath\""
}

fun getPathsText(params: List<ParameterData>): String {
    var pathData = "\npaths=listOf("

    pathData += params.filter { it.hasAnnotation<Path>() }.map { myParam ->
        val paramName = myParam.name
        val pathAnnotation = myParam.findAnnotationOrNull<Path>()
        val pathPath = pathAnnotation?.value ?: ""
        val pathEncoded = pathAnnotation?.encoded ?: false

        "${pathDataClass.name}(\"$pathPath\",$pathEncoded,\"\$$paramName\")"
    }.joinToString { it }


    pathData = "$pathData)"
    return pathData
}