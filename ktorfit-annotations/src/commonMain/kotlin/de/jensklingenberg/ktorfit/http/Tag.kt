package de.jensklingenberg.ktorfit.http

/**
 * Adds the argument instance as a request tag using the type as a AttributeKey.
 *
 * ```
 * @GET("/")
 * fun foo(@Tag tag: String)
 * ```
 *
 * Tag arguments may be `null` which will omit them from the request.
 *
 * @param value Will be used as the name for the attribute key. The default value will be replaced with the name of the parameter that is annotated.
 *
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Tag(
    val value: String = "KTORFIT_DEFAULT_VALUE"
)
