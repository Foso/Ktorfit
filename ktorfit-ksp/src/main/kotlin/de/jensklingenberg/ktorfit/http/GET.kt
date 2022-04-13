package de.jensklingenberg.ktorfit.http

/** Make a GET request.
 * @param value relative url path, if empty, you need to have a parameter with [Url]
 * */
@Target(AnnotationTarget.FUNCTION)
annotation class GET(val value: String = "")