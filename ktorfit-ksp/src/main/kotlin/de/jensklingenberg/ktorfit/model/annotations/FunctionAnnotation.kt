package de.jensklingenberg.ktorfit.model.annotations

enum class HttpMethod(val keyword: String) {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), HEAD("HEAD"), PATCH("PATCH"), CUSTOM("")
}



/**
 * Annotation at a function
 */
open class FunctionAnnotation

class Headers(val path: List<String>) : FunctionAnnotation()
class FormUrlEncoded : FunctionAnnotation()
class Streaming : FunctionAnnotation()
class Multipart : FunctionAnnotation()