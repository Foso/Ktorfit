package de.jensklingenberg.ktorfit.http

/** Make a POST request.
 * @param value relative url path, if empty, you need to have a parameter with [Url]
 * */
@Target(AnnotationTarget.FUNCTION)
annotation class POST(val value: String)