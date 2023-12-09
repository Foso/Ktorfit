package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*

/**
 * Converter for [Response]
 */
internal class KtorfitDefaultConverterFactory : Converter.Factory {

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == Response::class) {
            return DefaultResponseClassSuspendConverter(typeData, ktorfit)
        }
        return null
    }
}
