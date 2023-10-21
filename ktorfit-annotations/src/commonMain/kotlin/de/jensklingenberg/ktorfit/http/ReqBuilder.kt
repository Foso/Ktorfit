package de.jensklingenberg.ktorfit.http

/**
 * This can be used to add additional configurations to a request
 * the parameter type has to be HttpRequestBuilder.() -> Unit
 *
 * ```
 * @GET("posts")
 * suspend fun getPosts(@ReqBuilder builder : HttpRequestBuilder.() -> Unit) : List<Post>
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ReqBuilder