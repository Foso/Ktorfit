package de.jensklingenberg.ktorfit.converter.request

import kotlin.reflect.KClass

interface RequestConverter {

    fun supportedType(parameterType: KClass<*>, requestType: KClass<*>): Boolean

    fun convert(data: Any): Any
}