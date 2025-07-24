package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.annotations.CustomHttp
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation

fun getMethodCode(httpMethod: HttpMethodAnnotation): String {
    val httpMethodValue =
        if (httpMethod is CustomHttp) {
            httpMethod.customValue
        } else {
            httpMethod.httpMethod.keyword
        }
    return "this.method = HttpMethod.parse(\"${httpMethodValue}\")"
}
