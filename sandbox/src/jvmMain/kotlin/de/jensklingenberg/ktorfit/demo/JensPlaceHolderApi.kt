package de.jensklingenberg.ktorfit.demo

import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

interface JensPlaceHolderApi {

    companion object {
        const val baseUrl = "https://jsonplaceholder.typicode.com/"
    }

    @GET("posts")
    fun getPosts(): Flow<String>



    @Headers(value = ["Content-Type: application/json"])
    @DELETE("posts/{postId}")
    suspend fun deletePosts(@Path("postId") postId: Int): String

}