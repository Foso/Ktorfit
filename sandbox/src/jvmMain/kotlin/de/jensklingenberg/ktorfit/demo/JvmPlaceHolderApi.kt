package de.jensklingenberg.ktorfit.demo

import com.example.api.Response
import com.example.api.StarWarsApi
import com.example.model.People
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.core.TypeConverters
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.QueryName
import de.jensklingenberg.ktorfit.http.Streaming
import de.jensklingenberg.ktorfit.http.Url
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement

@TypeConverters(ResponseConverter::class)
internal interface JvmPlaceHolderApi : StarWarsApi {
    @GET("people/{id}/")
    suspend fun getPersonById2(
        @Path("id") peopleId: Int
    ): People

    @GET("people/{id}/")
    suspend fun testQuery(
        @Path("id") peopleId: Int,
        @Query world: String? = "World"
    ): People

    @GET("people/{id}/")
    suspend fun testQueryName(
        @Path("id") peopleId: Int,
        @QueryName na: List<String?>?
    ): People

    @GET("people/{id}/")
    suspend fun testQueryName2(
        @Path("id") peopleId: Int,
        @QueryName na: Map<String, String>?,
        @QueryMap na2: Map<String, String>?
    ): People

    @Streaming
    @GET("people/1/")
    suspend fun getPostsStreaming(): HttpStatement

    @GET("people/{id}/")
    fun getPersonById2AsResponse(
        @Path("id") peopleId: Int
    ): Response<People>

    @Headers(value = ["Content-Type: application/json"])
    @GET("people/{id}/")
    suspend fun callPersonById2AsResponse(
        @Path("id") peopleId: Int
    ): Call<List<People>>

    @GET()
    suspend fun getPersonByIdByUrl(
        @Url peopleId: String,
        @QueryMap name: Map<String, Int>?
    ): People
}

class ResponseConverter {

    suspend fun toResponse(getResponse: suspend () -> HttpResponse): Response<People> {
        val httpResponse = getResponse()
        val people = httpResponse.body<People>()
        return Response.success(people)
    }
}
