package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.Response
import io.ktor.client.statement.HttpResponse

/**
 * Converter for [Response]
 */
public class ResponseConverterFactory : Converter.Factory {
    override fun suspendResponseConverter(
        typeData2: TypeData2,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData2.typeInfo.type == Response::class) {
            return ResponseClassSuspendConverter(typeData2, ktorfit)
        }
        return null
    }
}
