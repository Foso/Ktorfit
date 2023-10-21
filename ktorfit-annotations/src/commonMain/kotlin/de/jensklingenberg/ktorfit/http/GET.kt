package de.jensklingenberg.ktorfit.http

/** Make a GET request.
 * ```
 * @GET("issue")
 * suspend fun getIssue(@Query("id") id: String) : Issue
 * ```
 * @param value relative url path, if empty, you need to have a parameter with [Url]
 * */
@Target(AnnotationTarget.FUNCTION)
annotation class GET(val value: String = "")