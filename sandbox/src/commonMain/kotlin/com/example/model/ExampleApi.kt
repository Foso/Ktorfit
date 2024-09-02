package com.example.model

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo

interface ExampleApi {
    @GET("example.json")
    suspend fun getUser(): Response<User>

    @GET("example.json")
    suspend fun getUserResponse(): MyOwnResponse<User>
}

annotation class TypeInfo

interface Test4 {
    suspend fun <T> test(
        @com.example.model.TypeInfo typeInfo: TypeInfo
    ): T
}

class Test3(
    val httpClient: HttpClient
) : Test4 {
    override suspend fun <T> test(
        @de.jensklingenberg.ktorfit.http.TypeInfo("T") typeInfo: TypeInfo
    ): T {
        val user = httpClient.get("https://jsonplaceholder.typicode.com/users/1")
        return user.call.body(typeInfo) as T
    }
}

suspend inline fun <reified T> Test4.download(typeInfo: TypeInfo = typeInfo<T>()): T = this.test(typeInfo)
