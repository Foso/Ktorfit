package de.jensklingenberg.ktorfit.converter.request

import kotlin.reflect.KClass

public interface RequestConverter {

    public fun supportedType(parameterType: KClass<*>, requestType: KClass<*>): Boolean

    public fun convert(data: Any): Any
}