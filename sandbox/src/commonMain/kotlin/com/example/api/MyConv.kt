package com.example.api

import com.example.model.Envelope
import com.example.model.MyOwnResponse
import com.example.model.People
import com.example.model.User
import com.example.model.github.GithubFollowerResponseItem
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


class MyConv {

    suspend fun <T> _conv( httpResponse: HttpResponse): User {
        val env = httpResponse.body<Envelope>()
        return env.user
    }

    public fun convert(
        getResponse: suspend () -> HttpResponse,
        typeInfo: TypeInfo,
        httpClient: HttpClient
    ): Flow<Any> {
        // Implementation here
        return flow {

            if ((typeInfo.kotlinType?.arguments[0]?.type?.classifier as KClass<*>) == User::class) {
                val response = getResponse()
                val user = response.body<Envelope>().user
                emit(user)
                return@flow
            }
            val response = getResponse()


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

    suspend inline fun <T> toResponse(httpResponse: HttpResponse): MyOwnResponse<User> {
        val user = httpResponse.body<Envelope>()
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