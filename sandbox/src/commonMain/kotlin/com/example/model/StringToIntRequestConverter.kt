package com.example.model

import de.jensklingenberg.ktorfit.converter.request.RequestConverter
import kotlin.reflect.KClass

class StringToIntRequestConverter : RequestConverter {
    override fun supportedType(parameterType: KClass<*>, requestType: KClass<*>): Boolean {
        val parameterIsString = parameterType == String::class
        val requestIsInt = requestType == Int::class
        return parameterIsString && requestIsInt
    }

    override fun convert(data: Any): Any {
        return (data as String).toInt()
    }
}

class StringToIntRequestConverter2 : RequestConverter {
    override fun supportedType(parameterType: KClass<*>, requestType: KClass<*>): Boolean {
        val parameterIsString = parameterType == Int::class
        val requestIsInt = requestType == String::class
        return parameterIsString && requestIsInt
    }

    override fun convert(data: Any): Any {
        return "5"
    }
}