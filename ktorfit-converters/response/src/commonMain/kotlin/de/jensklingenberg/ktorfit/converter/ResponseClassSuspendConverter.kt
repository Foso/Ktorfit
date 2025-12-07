package de.jensklingenberg.ktorfit.converter

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.Response
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

internal class ResponseClassSuspendConverter(private val typeData2: TypeData2, private val ktorfit: Ktorfit) :
    Converter.SuspendResponseConverter<HttpResponse, Response<Any?>> {
    override suspend fun convert(result: KtorfitResult): Response<Any?> {
        return when (result) {
            is KtorfitResult.Success -> {
                val typeInfo = typeData2.typeArgs.first().typeInfo
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
                        val convertedBody =
                            ktorfit.nextSuspendResponseConverter(
                                null,
                                typeData2.typeArgs.first(),
                            )?.convert(result)
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
