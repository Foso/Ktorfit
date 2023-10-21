package de.jensklingenberg.ktorfit.http

/** Make a request with a custom HTTP method.
 * ```
 * @HTTP(method = "CUSTOM", path = "custom/endpoint/")
 * suspend fun getIssue(@Query("id") id: String) : Issue
 * ```
 * @param method HTTP method verb.
 * @param path URL path.
 * @param hasBody
 * */
@Target(AnnotationTarget.FUNCTION)
annotation class HTTP(val method: String, val path: String = "", val hasBody: Boolean = false)


