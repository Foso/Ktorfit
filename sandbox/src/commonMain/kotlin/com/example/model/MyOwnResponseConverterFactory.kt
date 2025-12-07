package com.example.model

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData2
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

class MyOwnResponseConverterFactory : Converter.Factory {
    override fun suspendResponseConverter(
        typeData2: TypeData2,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData2.typeInfo.type == MyOwnResponse::class) {
            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(result: KtorfitResult): Any {
                    return when (result) {
                        is KtorfitResult.Failure -> {
                            MyOwnResponse.error(result.throwable)
                        }

                        is KtorfitResult.Success -> {
                            val response = result.response
                            return try {
                                val convertedBody =
                                    ktorfit
                                        .nextSuspendResponseConverter(
                                            null,
                                            typeData2.typeArgs.first()
                                        )?.convert(result)
                                        ?: response.body(typeData2.typeArgs.first().typeInfo)
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
