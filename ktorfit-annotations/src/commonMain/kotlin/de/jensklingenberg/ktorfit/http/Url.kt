package de.jensklingenberg.ktorfit.http

/**
 * ```
 * @GET
 * suspend fun request(@Url url: String): List<Comment>
 * ```
 */
@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Url
