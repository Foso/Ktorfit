package de.jensklingenberg.ktorfit.http

/**
 *
 * ```
 *  @Streaming
 *  @GET("posts")
 *  suspend fun getPostsStreaming(): HttpStatement
 * ```
 *
 *  The return type has to be HttpStatement
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class Streaming
