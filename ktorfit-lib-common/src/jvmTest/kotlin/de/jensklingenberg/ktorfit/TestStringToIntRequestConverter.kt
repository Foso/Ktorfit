package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.converter.request.RequestConverter
import kotlin.reflect.KClass

class TestStringToIntRequestConverter : RequestConverter {
    override fun supportedType(parameterType: KClass<*>, requestType: KClass<*>): Boolean {
        val parameterIsString = parameterType == String::class
        val requestIsInt = requestType == Int::class
        return parameterIsString && requestIsInt
    }

    override fun convert(data: Any): Any {
        return (data as String).toInt()
    }
}


