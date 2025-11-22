package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Path
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Url
import de.jensklingenberg.ktorfit.model.baseUrl
import de.jensklingenberg.ktorfit.model.ktorfitClass

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
                "(${baseUrl.objectName}.takeIf{ !${parameterData.name}.startsWith(\"http\")} ?: \"\") + "
            } ?: "${baseUrl.objectName} + "
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
