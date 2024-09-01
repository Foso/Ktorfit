package de.jensklingenberg.ktorfit.http

/**
 * This can be set if you have parts in your URL that want to dynamically replaced
 *
 * @param value The default value will be replaced with the name of the parameter that is annotated.
 * When the URL of an HTTP Method Annotation contains curly braces, they will be replaced with the value of
 * the corresponding parameter that has a matching [value].
 * @param encoded true means that this value is already URL encoded and will not be encoded again
 * Path parameters type may not be nullable.
 *
 *
 * ```
 * @GET("post/{postId}")
 * suspend fun getPosts(@Path("postId") postId: Int): List<Post>
 * ```
 */

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path(
    val value: String = "KTORFIT_DEFAULT_VALUE",
    val encoded: Boolean = false
)
