package com.example.api

import com.example.model.Envelope
import com.example.model.MyOwnResponse
import com.example.model.People
import com.example.model.User
import com.example.model.github.GithubFollowerResponseItem
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class MyConv {

    suspend fun <T> _conv(httpResponse: HttpResponse): User {
        val env = httpResponse.body<Envelope>()
        return env.user
    }

    public inline fun <reified T> convert(noinline getResponse: suspend () -> HttpResponse): Flow<User> {

        // Implementation here
        return flow {
            val response = getResponse()
            val user = response.body<Envelope>().user
            emit(user)
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

    suspend inline fun toResponse(httpResponse: HttpResponse): MyOwnResponse<User> {
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