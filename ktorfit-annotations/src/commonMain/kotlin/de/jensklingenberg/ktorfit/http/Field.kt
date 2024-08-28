package de.jensklingenberg.ktorfit.http

/**
 * Needs to be used in combination with [FormUrlEncoded]
 * @param value The default value will be replaced with the name of the parameter that is annotated.
 * @param encoded true means that this value is already URL encoded and will not be encoded again
 * @see FormUrlEncoded
 * @see FieldMap
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Field(
    val value: String = "KTORFIT_DEFAULT_VALUE",
    val encoded: Boolean = false
)
