package de.jensklingenberg.ktorfit.http

/** Make a PATCH request.
 * ```
 * @PATCH("issue")
 * suspend fun patchIssue(@Body issue: Issue)
 * ```
 * @param value relative url path, if empty, you need to have a parameter with [Url]
 *
 */
@Target(AnnotationTarget.FUNCTION)
annotation class PATCH(
    val value: String = ""
)
