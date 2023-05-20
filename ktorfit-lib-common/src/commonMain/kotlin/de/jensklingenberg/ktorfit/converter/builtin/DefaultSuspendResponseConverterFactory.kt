package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*

/**
 * Will be used when no other suspend converter was found
 * It is automatically applied last
 */
internal class DefaultSuspendResponseConverterFactory : Converter.Factory {

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
            override suspend fun convert(response: HttpResponse): Any {
                return try {
                    response.call.body(typeData.typeInfo)
                } catch (exception: Exception) {
                    throw exception
                }
            }

        }
    }
}