package de.jensklingenberg.ktorfit.http

/**
 * This can be set if you have parts in your URL that want to dynamically replaced
 *
 * @param value When the URL of an HTTP Method Annotation contains curly braces. They will be replaced with the value of
 * the corresponding parameter that has a matching [value]
 * @param encoded true means that this value is already URL encoded and will not be encoded again
 * <p>Path parameters type may not be nullable.
 *
 *
 * <pre><code>
 * @GET("post/{postId}")
 * suspend fun getPosts(@Path("postId") postId: Int): List< Post>
 * </code></pre>
 */

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path(val value: String, val encoded: Boolean = false)