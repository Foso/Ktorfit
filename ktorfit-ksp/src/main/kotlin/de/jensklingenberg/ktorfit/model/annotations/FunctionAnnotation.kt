package de.jensklingenberg.ktorfit.model.annotations

enum class HttpMethod(
    val keyword: String
) {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    PATCH("PATCH"),
    CUSTOM(""),
}

/**
 * Annotation at a function
 */
open class FunctionAnnotation

class Headers(
    val value: List<String>
) : FunctionAnnotation()

object FormUrlEncoded : FunctionAnnotation()

object Streaming : FunctionAnnotation()

object Multipart : FunctionAnnotation()
