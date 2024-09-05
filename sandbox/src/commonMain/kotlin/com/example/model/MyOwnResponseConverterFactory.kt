package com.example.model

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

class MyOwnResponseConverterFactory : Converter.Factory {
    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == MyOwnResponse::class) {
            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(result: KtorfitResult): Any {
                    return when (result) {
                        is KtorfitResult.Failure -> {
                            MyOwnResponse.error(result.throwable)
                        }

                        is KtorfitResult.Success -> {
                            val response = result.response
                            val te = typeData.typeInfo.kotlinType?.arguments
                            val type1 = te?.get(0)?.type
                            val re = type1?.classifier == (User::class)
                            return try {
                                val type = typeData.typeArgs.first()

                                val convertedBody =
                                    ktorfit
                                        .nextSuspendResponseConverter(
                                            null,
                                            type
                                        )?.convert(result)
                                        ?: response.body(type.typeInfo)
                                MyOwnResponse.success(convertedBody)
                            } catch (ex: Throwable) {
                                print(ex)
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
