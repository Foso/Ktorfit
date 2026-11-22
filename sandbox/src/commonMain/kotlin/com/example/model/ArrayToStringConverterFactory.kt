package com.example.model

import de.jensklingenberg.ktorfit.converter.Converter
import kotlin.reflect.KClass

class ArrayToStringConverterFactory : Converter.Factory {
    private fun supportedType(
        data: Any,
        requestType: KClass<*>
    ): Boolean {
        val parameterIsList = List::class.isInstance(data)
        val requestIsString = requestType == String::class
        return parameterIsList && requestIsString
    }

    override fun requestParameterConverter(
        parameterType: KClass<*>,
        requestType: KClass<*>
    ): Converter.RequestParameterConverter {
        return object : Converter.RequestParameterConverter {
            override fun convert(data: Any): Any {
                return if (supportedType(data, requestType)) {
                    (data as? List<*>)?.joinToString(",")!!
                } else {
                    data
                }
            }
        }
    }
}