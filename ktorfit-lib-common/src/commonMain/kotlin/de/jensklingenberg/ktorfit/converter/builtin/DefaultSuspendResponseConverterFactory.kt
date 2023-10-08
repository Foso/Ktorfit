package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*

/**
 * Will be used when no other suspend converter was found
 * It is automatically applied last
 */
internal class DefaultSuspendResponseConverterFactory : Converter.Factory {

    class DefaultSuspendResponseConverter(val typeData: TypeData) :
        Converter.SuspendResponseConverter<HttpResponse, Any> {
        override suspend fun convert(response: HttpResponse): Any {
            return convert(KtorfitResult.Success(response))
        }

        override suspend fun convert(result: KtorfitResult): Any {
            return when (result) {
                is KtorfitResult.Failed -> {
                    throw result.throwable
                }

                is KtorfitResult.Success -> {
                    val response = result.response
                    response.call.body(typeData.typeInfo)
                }
            }
        }
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *> {
        return DefaultSuspendResponseConverter(typeData)
    }

}

