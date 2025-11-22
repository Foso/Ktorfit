package com.example.api

import com.example.model.Comment
import com.example.model.MyOwnResponse
import com.example.model.Post
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.core.TypeConverters
import de.jensklingenberg.ktorfit.http.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@TypeConverters(MyCallConverter::class)
interface JsonPlaceHolderApi {
    companion object {
        const val baseUrl = "https://jsonplaceholder.typicode.com/"
    }

    @GET("posts")
    fun getPosts(): Flow<String>

    @GET("posts")
    fun callPosts(): Call<List<Post>>

    @HTTP("GET2", "posts")
    fun callPostsCustomHttp(): Call<List<Post>>

    @GET("posts")
    fun suscallPosts(): Call<String>

    @Streaming
    @GET("docs/response.html#streaming")
    suspend fun getPostsStreaming(
        @QueryMap test: Map<String, String>
    ): HttpStatement

    @GET("posts/{postId}")
    suspend fun getPostById(
        @Path postId: Int = 4,
        @Tag value: String = "test"
    ): Post

    @GET("posts/{postId}/comments")
    fun getFlowCommentsByPostId(
        @Path("postId") postId: Int,
        @ReqBuilder builder: HttpRequestBuilder.() -> Unit
    ): Flow<List<Comment>>?

    @GET("posts/{postId}/comments")
    suspend fun getCommentsByPostId(
        @Path("postId") postId: Int
    ): List<Comment>?

    @GET("posts/{postId}/comments")
    suspend fun getCommentsByPostIdResponse(
        @RequestType(Int::class) @Path("postId") postId: String
    ): MyOwnResponse<List<Comment>>

    @Headers(value = ["Content-Type: application/json"])
    @GET("posts/{postId}/comments")
    fun callCommentsByPostId(
        @Path("postId") postId: Int
    ): Call<List<Comment>>

    @Headers(value = ["Content-Type: application/json"])
    @GET("posts/{postId}/comments")
    suspend fun resCommentsByPostId(
        @Path("postId") postId: Int
    ): Response<List<Comment>>

    @Headers(value = ["Content-Type: application/json"])
    @GET("posts/{postId}/comments")
    fun deferedCommentsByPostId(
        @Path("postId") postId: Int
    ): Deferred<List<Comment>>

    @Headers(value = ["Content-Type: application/json"])
    @GET("comments")
    fun getCommentsByPostIdQuery(
        @QueryName(false) postId: List<String>,
    ): Call<List<Comment>>

    @Headers(value = ["Content-Type: application/json"])
    @POST("posts")
    suspend fun postPosts(
        @Body post: Post
    ): Post

    @Headers(value = ["Content-Type: application/json"])
    @POST("posts")
    suspend fun putPosts(
        @Body post: Post
    ): Post

    @Headers(value = ["Content-Type: application/json"])
    @PATCH("posts/{postId}/{number}")
    suspend fun patchPosts(
        @Path("postId") postId: Int
    ): Post

    @Headers(value = ["Content-Type: application/json"])
    @DELETE("posts/{postId}")
    suspend fun deletePosts(
        @Path("postId") postId: Int
    ): String
}

class MyCallConverter() {
    inline fun <reified T : Any> convert(noinline getResponse: suspend () -> HttpResponse): Flow<Any> {
        val t = T::class
        return flow {
            val httpResponse = getResponse()
            val responseBody = httpResponse.body<T>()
            emit(responseBody)
        }
    }

    inline fun <reified T> convert2(noinline getResponse: suspend () -> HttpResponse, typeInfo: TypeInfo): Call<Any> {

        return object : Call<Any> {
            override fun onExecute(callBack: Callback<Any>) {


            }

        }

    }

}
