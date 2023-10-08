package com.example.model

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*


class MyOwnResponseConverterFactory : Converter.Factory {

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == MyOwnResponse::class) {

            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(response: HttpResponse): Any {
                    return convert(KtorfitResult.Success(response))
                }

                override suspend fun convert(result: KtorfitResult): Any {
                    return when (result) {
                        is KtorfitResult.Failed -> {
                            MyOwnResponse.error(result.throwable)
                        }

                        is KtorfitResult.Success -> {
                            val response = result.response
                            return try {
                                val convertedBody = ktorfit.nextSuspendResponseConverter(
                                    null,
                                    typeData.typeArgs.first()
                                )?.convert(result)
                                    ?: response.body(typeData.typeArgs.first().typeInfo)
                                MyOwnResponse.success(convertedBody)
                            } catch (ex: Throwable) {
                                MyOwnResponse.error(ex)
                            }
                        }
                    }
                }
            }
        }
        return null
    }
}