package de.jensklingenberg.ktorfit.http

/** Make a DELETE request.
 * @param value relative url path, if empty, you need to have a parameter with [Url]
 * */
@Target(AnnotationTarget.FUNCTION)
annotation class DELETE(val value: String = "")