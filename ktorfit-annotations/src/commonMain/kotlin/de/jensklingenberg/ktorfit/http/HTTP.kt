package de.jensklingenberg.ktorfit.http

/** Make a request with a custom HTTP method. */
@Target(AnnotationTarget.FUNCTION)
annotation class HTTP(val method: String, val path: String = "", val hasBody: Boolean = false)


