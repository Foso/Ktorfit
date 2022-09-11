package de.jensklingenberg.ktorfit.http

/**
 * Add a header to a request
 *
 * @ GET("comments")
 *
 * suspend fun request( @ Header("Content-Type") name: String): List< Comment>
 *
 * A request with request("Hello World") will have the header "Content-Type:Hello World"
 * header with null values will be ignored
 * @see Headers
 * @see HeaderMap
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Header(val value: String)