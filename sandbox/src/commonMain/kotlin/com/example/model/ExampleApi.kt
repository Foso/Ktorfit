package com.example.model

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.ReturnType
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo

interface BaseApi<T> {
    suspend fun getUser3(): Response<T>
}

interface ExampleApi {
    companion object {
        const val baseUrl = "https://foso.github.io/Ktorfit/"
    }

    @GET("example.json")
    suspend fun getUser(): Response<User>

    @GET("example.json")
    suspend fun <T, S> getUser2(
        @ReturnType typeInfo: TypeInfo,
        @Query query: List<S>
    ): T

    @GET("example.json")
    suspend fun <T : User> getUserResponse(): MyOwnResponse<T>
}

interface Test4 {
    suspend fun <T> download(
        @ReturnType returnType: TypeInfo
    ): T
}

class Test3(
    val httpClient: HttpClient
) : Test4 {
    override suspend fun <T> download(
        @ReturnType returnType: TypeInfo
    ): T {
        val user = httpClient.get("https://jsonplaceholder.typicode.com/users/1")
        return user.call.body(returnType) as T
    }
}

suspend inline fun <reified T> Test4.download(): T = this.download(typeInfo<T>())
