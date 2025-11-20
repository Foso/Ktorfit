package de.jensklingenberg.ktorfit.core

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class TypeConverters(
    vararg val value: KClass<*>
)

@Target(AnnotationTarget.FUNCTION)
annotation class TypeConverter