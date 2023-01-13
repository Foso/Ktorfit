package com.example.api


import de.jensklingenberg.ktorfit.http.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow


interface JsonPlaceHolderApi {

    companion object {
        const val baseUrl = "https://jsonplaceholder.typicode.com/"
    }

    @GET("posts")
    fun getPosts(): Flow<String>



    @Headers(value = ["Content-Type: application/json"])
    @DELETE("posts/{postId}")
    suspend fun deletePosts(@Path("postId") postId: Int): String

}
