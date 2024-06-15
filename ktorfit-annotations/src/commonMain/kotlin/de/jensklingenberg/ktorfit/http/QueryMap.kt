package de.jensklingenberg.ktorfit.http

/**
 *  Used for query parameters
 *
 *  * <p>A {@code null} value for the map, as a key is not allowed.
 *  @param encoded true means that this value is already URL encoded and will not be encoded again

 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class QueryMap(val encoded: Boolean = false)
