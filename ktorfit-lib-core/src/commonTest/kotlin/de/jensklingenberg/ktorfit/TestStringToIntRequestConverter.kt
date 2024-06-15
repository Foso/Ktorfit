package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.converter.Converter
import kotlin.reflect.KClass

class TestStringToIntRequestConverter : Converter.Factory {
    private fun supportedType(
        parameterType: KClass<*>,
        requestType: KClass<*>,
    ): Boolean {
        val parameterIsString = parameterType == String::class
        val requestIsInt = requestType == Int::class
        return parameterIsString && requestIsInt
    }

    class Test : Converter.RequestParameterConverter {
        override fun convert(data: Any): Any {
            return (data as String).toInt()
        }
    }

    override fun requestParameterConverter(
        parameterType: KClass<*>,
        requestType: KClass<*>,
    ): Converter.RequestParameterConverter? {
        if (!supportedType(parameterType, requestType)) return null
        return Test()
    }
}
