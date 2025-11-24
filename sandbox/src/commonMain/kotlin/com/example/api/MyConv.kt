package com.example.api

import com.example.model.Envelope
import com.example.model.MyOwnResponse
import com.example.model.People
import com.example.model.User
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.util.AttributeKey
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


class MyConv {

    suspend fun <T> _conv(httpResponse: suspend () -> HttpResponse): User {
        val test = httpResponse()
        val env = test.body<Envelope>()
        return env.user
    }

    inline fun <reified T> convert(
        noinline getResponse: suspend () -> HttpResponse,
        typeInfo: TypeInfo,
    ): Flow<Any> {
        // Implementation here


        val t = (typeInfo<T>().kotlinType?.arguments?.first()?.type?.classifier as KClass<*>)

        return flow {
            val response = getResponse()
            val attr = response.request.attributes[AttributeKey<String>("hallo")]
            if ((typeInfo.kotlinType?.arguments?.first()?.type?.classifier as KClass<*>) == User::class) {

                val user = response.body<Envelope>().user
                emit(user)
                return@flow
            }
            val user = response.call.body(TypeInfo(t))
            emit(user)
            return@flow

        }
    }

    fun convertCall(getResponse: suspend () -> HttpResponse): Call<People> {
        return object : Call<People> {
            override fun onExecute(callBack: Callback<People>) {
                kotlinx.coroutines.GlobalScope.launch {
                    try {
                        val response = getResponse()
                        val people = response.body<People>()
                        callBack.onResponse(people, response)
                    } catch (ex: Exception) {
                        callBack.onError(ex)
                    }
                }
            }

        }
    }

    suspend inline fun <T> toResponse(httpResponse: suspend () -> HttpResponse): MyOwnResponse<User> {
        val response = httpResponse()
        val user = response.body<Envelope>()
        return MyOwnResponse.success(user.user)
    }

    fun getFollowers(getResponse: suspend () -> HttpResponse): Flow<List<Any>> {
        // Implementation here
        return flow {
            // Dummy implementation, replace with actual logic
            emit(emptyList())
        }
    }
}