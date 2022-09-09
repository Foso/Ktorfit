package de.jensklingenberg.ktorfit.model.annotations

open class HttpMethodAnnotation(open val path: String, open val httpMethod: HttpMethod) : FunctionAnnotation()

class CustomHttp(override val path: String, override val httpMethod: HttpMethod, val hasBody: Boolean = false) :
    HttpMethodAnnotation(path, httpMethod)