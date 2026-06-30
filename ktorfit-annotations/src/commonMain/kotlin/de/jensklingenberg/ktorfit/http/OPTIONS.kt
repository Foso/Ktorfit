package de.jensklingenberg.ktorfit.http

/** Make an OPTIONS request.
 *
 * @param value relative url path, if empty, you need to have a parameter with [Url]
 * */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class OPTIONS(
    val value: String = ""
)
