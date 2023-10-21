package de.jensklingenberg.ktorfit.http

/** Make a DELETE request.
 *
 * ```
 * @DELETE("deleteIssue")
 * fun deleteIssue(@Query("id") id: String)
 * ```
 * @param value relative url path, if empty, you need to have a parameter with [Url]
 *
 */
@Target(AnnotationTarget.FUNCTION)
annotation class DELETE(val value: String = "")