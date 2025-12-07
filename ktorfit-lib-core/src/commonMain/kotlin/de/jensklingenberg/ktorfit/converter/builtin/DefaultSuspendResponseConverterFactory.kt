package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData2
import io.ktor.client.statement.HttpResponse

/**
 * Will be used when no other suspend converter was found
 * It is automatically applied last
 */
internal class DefaultSuspendResponseConverterFactory : Converter.Factory {
    class DefaultSuspendResponseConverter(
        val typeData2: TypeData2
    ) : Converter.SuspendResponseConverter<HttpResponse, Any?> {
        override suspend fun convert(result: KtorfitResult): Any? =
            when (result) {
                is KtorfitResult.Failure -> {
                    if (typeData2.isNullable) {
                        null
                    } else {
                        throw result.throwable
                    }
                }

                is KtorfitResult.Success -> {
                    result.response.call.body(typeData2.typeInfo)
                }
            }
    }

    class DefaultResponseConverter : Converter.ResponseConverter<HttpResponse, Any?> {
        override fun convert(getResponse: suspend () -> HttpResponse): Any? = null
    }

    override fun suspendResponseConverter(
        typeData2: TypeData2,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, *> = DefaultSuspendResponseConverter(typeData2)

    override fun responseConverter(
        typeData2: TypeData2,
        ktorfit: Ktorfit,
    ): Converter.ResponseConverter<HttpResponse, *>? =
        if (typeData2.isNullable) {
            DefaultResponseConverter()
        } else {
            null
        }
}
