package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*

internal class DefaultResponseClassSuspendConverter(private val typeData: TypeData, private val ktorfit: Ktorfit) :
    Converter.SuspendResponseConverter<HttpResponse, Response<Any?>> {
    override suspend fun convert(response: HttpResponse): Response<Any?> {
        return convert(KtorfitResult.Success(response))
    }

    override suspend fun convert(result: KtorfitResult): Response<Any?> {
        return when (result) {
            is KtorfitResult.Success -> {
                val typeInfo = typeData.typeArgs.first().typeInfo
                val rawResponse = result.response
                val code: Int = rawResponse.status.value
                when {
                    code < 200 || code >= 300 -> {
                        val errorBody = rawResponse.body<Any>()
                        Response.error(errorBody, rawResponse)
                    }

                    code == 204 || code == 205 -> {
                        Response.success(null, rawResponse)
                    }

                    else -> {
                        val convertedBody = ktorfit.nextSuspendResponseConverter(
                            null,
                            typeData.typeArgs.first()
                        )?.convert(rawResponse)
                            ?: rawResponse.body(typeInfo)
                        Response.success(convertedBody, rawResponse)
                    }
                }
            }

            is KtorfitResult.Failure -> {
                throw result.throwable
            }
        }
    }
}