package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.Path
import de.jensklingenberg.ktorfit.model.annotations.Url


fun getUrlCode(params: List<ParameterData>, methodAnnotation: HttpMethodAnnotation): String {

    var urlPath = methodAnnotation.path.ifEmpty {
        params.firstOrNull { it.hasAnnotation<Url>() }?.let {
            "\${${it.name}}"
        } ?: ""
    }

    val baseUrl = if (methodAnnotation.path.startsWith("http")) {
        ""
    } else {
        params.firstOrNull { it.hasAnnotation<Url>() }?.let {
            "(ktorfitClient.baseUrl.takeIf{ !${it.name}.startsWith(\"http\")} ?: \"\") + "
        } ?: "ktorfitClient.baseUrl + "
    }


    params.filter { it.hasAnnotation<Path>() }.forEach { myParam ->
        val paramName = myParam.name
        val pathAnnotation = myParam.findAnnotationOrNull<Path>()!!
        val pathEncoded = if (!pathAnnotation.encoded) {
            ".encodeURLPath()"
        } else {
            ""
        }
        urlPath = urlPath.replace("{$paramName}", "\${\"\$${paramName}\"$pathEncoded}")
    }


    return "url(${baseUrl}\"$urlPath\")"
}