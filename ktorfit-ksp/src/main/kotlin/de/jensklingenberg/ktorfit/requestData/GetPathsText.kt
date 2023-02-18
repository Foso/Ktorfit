package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Path
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty

/**
 * Source for the "paths" argument of [de.jensklingenberg.ktorfit.RequestData]
 */
fun getPathsText(params: List<ParameterData>): String {
    val pathData = params.filter { it.hasAnnotation<Path>() }.map { myParam ->
        val paramName = myParam.name
        val pathAnnotation = myParam.findAnnotationOrNull<Path>()
        val pathPath = pathAnnotation?.value ?: ""
        val pathEncoded = pathAnnotation?.encoded ?: false

        "DH(\"$pathPath\",$paramName,$pathEncoded)"
    }.joinToString { it }

    return pathData.surroundIfNotEmpty("paths = listOf(", ")")
}