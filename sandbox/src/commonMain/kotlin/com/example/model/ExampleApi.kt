package com.example.model

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET

interface ExampleApi {
    @GET("example.json")
    suspend fun getUser(): Response<User>

    @GET("example.json")
    suspend fun getCallUser(): Call<User>

    @GET("example.json")
    suspend fun getCallUse3r()
}
