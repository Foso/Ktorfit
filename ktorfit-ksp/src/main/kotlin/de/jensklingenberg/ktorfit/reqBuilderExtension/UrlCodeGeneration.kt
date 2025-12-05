package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Path
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Url

fun getUrlCode(
    params: List<ParameterData>,
    methodAnnotation: HttpMethodAnnotation,
    queryCode: String,
): String {
    var urlPath =
        methodAnnotation.path.ifEmpty {
            params
                .firstOrNull { it.hasAnnotation<Url>() }
                ?.let {
                    "\${${it.name}}"
                }.orEmpty()
        }

    val baseUrl =
        if (methodAnnotation.path.startsWith("http")) {
            ""
        } else {
            params.firstOrNull { it.hasAnnotation<Url>() }?.let { parameterData ->
                "(_baseUrl.takeIf{ !${parameterData.name}.startsWith(\"http\")} ?: \"\") + "
            } ?: "_baseUrl + "
        }

    params.filter { it.hasAnnotation<Path>() }.forEach { parameterData ->
        val paramName = parameterData.name
        val pathAnnotation =
            parameterData.findAnnotationOrNull<Path>()
                ?: throw IllegalStateException("Path annotation not found")

        val pathEncoded =
            if (!pathAnnotation.encoded) {
                ".encodeURLPath()"
            } else {
                ""
            }
        urlPath = urlPath.replace("{${pathAnnotation.value}}", "\${\"\$${paramName}\"$pathEncoded}")
    }

    return "url{\ntakeFrom(${baseUrl}\"$urlPath\")\n" + queryCode + "}"
}
