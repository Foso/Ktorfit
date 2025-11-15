package com.example.model

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers

interface ExampleApi {

    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 Edg/132.0.0.0")
    @GET("example.json")
    suspend fun  getUser(): Response<User>

    @GET("example.json")
    suspend fun getUserResponse(): MyOwnResponse<User>
}
