package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*

/**
 * Converter for [Response]
 */
internal class DefaultResponseClassConverterFactory : Converter.Factory {

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == Response::class) {
            return object : Converter.SuspendResponseConverter<HttpResponse, Response<Any?>> {
                override suspend fun convert(response: HttpResponse): Response<Any?> {
                    val typeInfo = typeData.typeArgs.first().typeInfo
                    return try {
                        val rawResponse = response

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
                                val body = rawResponse.body<Any>(typeInfo)
                                Response.success(body, rawResponse)
                            }
                        }
                    } catch (exception: Exception) {
                        throw exception
                    }
                }

            }
        }
        return null
    }
}
