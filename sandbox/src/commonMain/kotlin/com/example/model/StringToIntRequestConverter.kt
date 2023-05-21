package com.example.model

import de.jensklingenberg.ktorfit.converter.Converter
import kotlin.reflect.KClass


class StringToIntRequestConverterFactory : Converter.Factory {

    class StringToIntRequestConverter : Converter.RequestParameterConverter {
        override fun convert(data: Any): Any {
            return (data as String).toInt()
        }
    }

    private fun supportedType(parameterType: KClass<*>, requestType: KClass<*>): Boolean {
        val parameterIsString = parameterType == String::class
        val requestIsInt = requestType == Int::class
        return parameterIsString && requestIsInt
    }

    override fun requestParameterConverter(
        parameterType: KClass<*>,
        requestType: KClass<*>
    ): Converter.RequestParameterConverter? {
        if (supportedType(parameterType, requestType)) {
            return object : Converter.RequestParameterConverter {
                override fun convert(data: Any): Any {
                    return (data as String).toInt()
                }
            }
        }
        return null
    }
}