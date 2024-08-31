package de.jensklingenberg.ktorfit.demo

import com.example.model.Post
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.flow.Flow

interface TestApi {
    @GET("pos4ts")
    fun getPosts(): Call<List<Post>>

    @GET("posts/{userId}")
    suspend fun getPost(
        @Path("userId") myUserId: Int = 4
    ): Post

    @POST("posts")
    suspend fun postPost(
        @Body otherID: Post
    ): Post

    @GET("posts/{userId}")
    suspend fun getPostsByUserId(
        @Path("userId") myUserId: Int
    ): List<Post>

    @Headers(value = ["Accept: application/json"])
    @GET("posts")
    fun getFlowPosts(): Flow<List<Post>>

    @POST("upload")
    suspend fun uppi(
        @Body map: MultiPartFormDataContent
    )
}
