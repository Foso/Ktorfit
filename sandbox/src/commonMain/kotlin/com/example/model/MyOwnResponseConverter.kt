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
                    return try {
                        val convertedBody = ktorfit.nextSuspendResponseConverter(
                            null,
                            typeData.typeArgs.first()
                        )?.convert(response)
                            ?: response.body(typeData.typeArgs.first().typeInfo)
                        MyOwnResponse.success(convertedBody)
                    } catch (ex: Throwable) {
                        MyOwnResponse.error(ex)
                    }
                }

                override suspend fun convert(ktorfitResult: KtorfitResult): Any {
                    return when (ktorfitResult) {
                        is KtorfitResult.Failed -> {
                            MyOwnResponse.error(ktorfitResult.throwable)
                        }

                        is KtorfitResult.Success -> {
                            convert(ktorfitResult.response)
                        }
                    }
                }
            }
        }
        return null
    }
}