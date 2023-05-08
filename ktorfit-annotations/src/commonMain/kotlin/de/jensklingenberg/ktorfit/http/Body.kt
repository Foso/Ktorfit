package de.jensklingenberg.ktorfit.http

/**
 *
Use this to upload data in an HTTP Body
@POST("createIssue")
fun upload(@Body issue: Issue)
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Body