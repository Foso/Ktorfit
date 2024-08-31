package de.jensklingenberg.ktorfit.http

/**
 * Used for query parameters
 *
 * ```
 * @GET("comments")
 * suspend fun getCommentsById(@Query("postId") postId: String): List<Comment>
 * ```
 * A request with getCommentsById(3) will result in the relative URL “comments?postId=3”
 *
 * ```
 * @GET("comments")
 * suspend fun getCommentsById(@Query("postId") postId: List<String?>): List<Comment>
 * ```
 *
 * A request with getCommentsById(listOf("3",null,"4")) will result in the relative URL “comments?postId=3&postId=4”
 *
 * @param value The default value will be replaced with the name of the parameter that is annotated.It is the key of the query parameter.
 * null values are ignored
 * @param encoded true means that this value is already URL encoded and will not be encoded again
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Query(
    val value: String = "KTORFIT_DEFAULT_VALUE",
    val encoded: Boolean = false
)
