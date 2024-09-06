package com.example

import com.example.model.Envelope
import com.example.model.User
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*

class UserFactory : Converter.Factory {
    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == User::class) {
            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(result: KtorfitResult): Any {
                    when (result) {
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

interface Jens1<T> {
    fun test(): T
}

interface Jens2<T> : Jens1<T>

class Jens3<T> : Jens2<T> by Jens4<T>()

class Jens4<T> : Jens2<T> {
    override fun test(): T = throw NullPointerException()
}
