package com.example.model

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET

interface ExampleApi {
    @GET("example.json")
    suspend fun getUser(): Response<User>

    @GET("example.json")
    suspend fun getUserMy(): MyOwnResponse<User>
}
