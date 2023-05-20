package de.jensklingenberg.ktorfit.converter.request

import de.jensklingenberg.ktorfit.converter.Converter
import kotlin.reflect.KClass

@Deprecated("Use Converter.RequestParameterConverter")
public interface RequestConverter : Converter.RequestParameterConverter {

    /**
     * Check if converter supports the types
     * @return true if this converter can convert [parameterType] to [requestType]
     */
    public override fun supportedType(parameterType: KClass<*>, requestType: KClass<*>): Boolean

    /**
     * Convert given [data]
     * @return the converted [data]
     */
    public override fun convert(data: Any): Any
}