package de.jensklingenberg.ktorfit.http

import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class RequestType(
    val requestType: KClass<*>
)
