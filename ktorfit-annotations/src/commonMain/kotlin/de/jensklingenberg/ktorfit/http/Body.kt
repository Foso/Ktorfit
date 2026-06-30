package de.jensklingenberg.ktorfit.http

/**
 * Use this to upload data in an HTTP Body
 *
 * ```
 * @POST("createIssue")
 * suspend fun upload(@Body issue: Issue)
 * ```
 */
@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Body
