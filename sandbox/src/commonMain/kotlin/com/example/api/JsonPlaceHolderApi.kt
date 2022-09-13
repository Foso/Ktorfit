package com.example.api

import com.example.model.Comment
import com.example.model.People
import com.example.model.Post
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.QueryName
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow

interface JsonPlaceHolderApi {

    companion object {
        const val baseUrl = "https://jsonplaceholder.typicode.com/"
    }

    @GET("posts")
    fun getPosts(): Flow<String>

    @GET("posts")
    fun callPosts(): Call<String>

    @GET("posts")
    suspend fun suscallPosts(): Call<String>

    @Streaming
    @GET("docs/response.html#streaming")
    suspend fun getPostsStreaming(@QueryMap test: Map<String, String>): HttpStatement

    @GET("posts/{postId}")
    suspend fun getPostById(@Path("postId") postId: Int = 4): Post

    @GET("posts/{postId}/comments")
    fun getCommentsByPostId(@Path("postId") postId: Int): Flow<List<Comment>>?

    @Headers(value = ["Content-Type: application/json"])
    @GET("comments")
    fun getCommentsByPostIdQuery(
        @QueryName(true) postId: List<String>,
        @HeaderMap() postId2: Map<String, *>
    ): Call<String>

    @Headers(value = ["Content-Type: application/json"])
    @POST("posts")
    suspend fun postPosts(@Body post: Post): Post

    @Headers(value = ["Content-Type: application/json"])
    @POST("posts")
    suspend fun putPosts(@Body post: Post): Post

    @Headers(value = ["Content-Type: application/json"])
    @PATCH("posts/{postId}/{number}")
    suspend fun patchPosts(@Path("postId") postId: Int): Post

    @Headers(value = ["Content-Type: application/json"])
    @DELETE("posts/{postId}")
    suspend fun deletePosts(@Path("postId") postId: Int): String

}

