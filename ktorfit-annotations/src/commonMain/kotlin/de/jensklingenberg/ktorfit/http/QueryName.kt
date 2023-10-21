package de.jensklingenberg.ktorfit.http

/**
 * Used for query parameters
 * @param encoded true means that this value is already URL encoded and will not be encoded again
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class QueryName(val encoded: Boolean = false)