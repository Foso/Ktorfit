package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.generator.pathDataClass
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Path
import de.jensklingenberg.ktorfit.surroundIfNotEmpty

fun getPathsText(params: List<ParameterData>): String {
    var pathData = ""

    pathData += params.filter { it.hasAnnotation<Path>() }.map { myParam ->
        val paramName = myParam.name
        val pathAnnotation = myParam.findAnnotationOrNull<Path>()
        val pathPath = pathAnnotation?.value ?: ""
        val pathEncoded = pathAnnotation?.encoded ?: false

        "${pathDataClass.name}(\"$pathPath\",$pathEncoded,\"\$$paramName\")"
    }.joinToString { it }.surroundIfNotEmpty("paths = listOf(", ")")

    return pathData
}