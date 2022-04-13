package de.jensklingenberg.ktorfit.http


/**
 * Needs to be used in combination with [FormUrlEncoded]
 *
 * @param encoded true means that this value is already URL encoded and will not be encoded again

 * @see FormUrlEncoded
 * @see FieldMap
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Field(val value: String, val encoded: Boolean = false)