package de.jensklingenberg.ktorfit.http


/**
 * If the type is List<PartData> the value will be used directly with its content type.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PartMap(val encoding: String = "binary")