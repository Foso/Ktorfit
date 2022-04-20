package de.jensklingenberg.ktorfit.model.annotations

enum class HttpMethod(val keyword: String) {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), HEAD("HEAD"), PATCH("PATCH"), CUSTOM("")
}

class CustomHttp(override val path: String, override val httpMethod: HttpMethod, val hasBody: Boolean = false) :
    HttpMethodAnnotation(path, httpMethod)

/**
 * Annotation at a function
 */
open class FunctionAnnotation
open class HttpMethodAnnotation(open val path: String, open val httpMethod: HttpMethod) : FunctionAnnotation()

class Headers(val path: List<String>) : FunctionAnnotation()
class FormUrlEncoded : FunctionAnnotation()
class Streaming : FunctionAnnotation()
class Multipart : FunctionAnnotation()