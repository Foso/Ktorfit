package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.statement.HttpResponse

/**
 * Will be used when no other suspend converter was found
 * It is automatically applied last
 */
internal class DefaultSuspendResponseConverterFactory : Converter.Factory {
    class DefaultSuspendResponseConverter(
        val typeData: TypeData
    ) : Converter.SuspendResponseConverter<HttpResponse, Any?> {
        override suspend fun convert(result: KtorfitResult): Any? =
            when (result) {
                is KtorfitResult.Failure -> {
                    if (typeData.isNullable) {
                        null
                    } else {
                        throw result.throwable
                    }
                }

                is KtorfitResult.Success -> {
                    result.response.call.body(typeData.typeInfo)
                }
            }
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, *> = DefaultSuspendResponseConverter(typeData)

    override fun responseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.ResponseConverter<HttpResponse, *>? =
        if (typeData.isNullable) {
            object : Converter.ResponseConverter<HttpResponse, Any?> {
                override fun convert(getResponse: suspend () -> HttpResponse): Any? = null
            }
        } else {
            null
        }
}
