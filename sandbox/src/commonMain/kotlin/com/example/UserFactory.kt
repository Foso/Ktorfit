package com.example

import com.example.model.Envelope
import com.example.model.User
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*

class UserFactory : Converter.Factory {

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == User::class) {
            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(response: HttpResponse): Any {
                    return convert(KtorfitResult.Success(response))
                }

                override suspend fun convert(result: KtorfitResult): Any {
                    return when (result) {
                        is KtorfitResult.Success -> {
                            val response = result.response
                            val envelope = response.body<Envelope>()
                            return envelope.user
                        }
                        is KtorfitResult.Failure -> {
                            throw result.throwable
                        }
                    }
                }
            }
        }
        return null
    }
}
