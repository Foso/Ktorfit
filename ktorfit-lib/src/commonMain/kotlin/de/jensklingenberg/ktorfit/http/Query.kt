package de.jensklingenberg.ktorfit.http


/**
 * Used for query parameters
 *
 * @ GET("comments")
 * suspend fun getCommentsById(@Query("postId") postId: String): List<Comment>
 *
 * A request with getCommentsById(3) will result in the relative URL “comments?postId=3”
 *
 * @ GET("comments")
 * suspend fun getCommentsById(@Query("postId") postId: List<String?>): List<Comment>
 *
 * A request with getCommentsById(listOf("3",null,"4")) will result in the relative URL “comments?postId=3&postId=4”
 *
 * =====
 * [value] is the key of the query parameter
 * @param encoded true means that this value is already URL encoded and will not be encoded again
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Query(val value: String, val encoded: Boolean = false)