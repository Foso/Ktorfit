package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResponse
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*

/**
 * Converter for [Response]
 */
internal class KtorfitDefaultConverterFactory : Converter.Factory {

    class DefaultResponseClassSuspendConverter(val typeData: TypeData, val ktorfit: Ktorfit) :
        Converter.SuspendResponseConverter<HttpResponse, Response<Any?>> {
        override suspend fun convert(response: HttpResponse): Response<Any?> {
            val typeInfo = typeData.typeArgs.first().typeInfo

            val rawResponse = response

            val code: Int = rawResponse.status.value

            return when {
                code < 200 || code >= 300 -> {
                    val errorBody = rawResponse.body<Any>()
                    Response.error(errorBody, rawResponse)
                }

                code == 204 || code == 205 -> {
                    Response.success<Any>(null, rawResponse)
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

        override suspend fun convert(ktorfitResponse: KtorfitResponse): Response<Any?> {
            val typeInfo = typeData.typeArgs.first().typeInfo
            return when (ktorfitResponse) {
                is KtorfitResponse.Success -> {
                    val rawResponse = ktorfitResponse.response

                    val code: Int = rawResponse.status.value
                    when {
                        code < 200 || code >= 300 -> {
                            val errorBody = rawResponse.body<Any>()
                            Response.error(errorBody, rawResponse)
                        }

                        code == 204 || code == 205 -> {
                            Response.success<Any>(null, rawResponse)
                        }

                        else -> {
                            val convertedBody = ktorfit.nextSuspendResponseConverter(
                                null,
                                typeData.typeArgs.first()
                            )?.convert(ktorfitResponse)
                                ?: rawResponse.body<Any>(typeInfo)
                            Response.success(convertedBody, rawResponse)
                        }
                    }
                }

                is KtorfitResponse.Failed -> {
                    throw ktorfitResponse.throwable
                }
            }
        }
    }

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
