package com.example.ktorfittest

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow

interface StarWarsApi {
    companion object {
        const val baseUrl = "https://swapi.info/api/"
    }

    @GET("people/{id}/")
    suspend fun getPersonByIdResponse(
        @Path("id") peopleId: Int
    ): Person

    @GET("people/{id}/")
    fun getPeopleByIdFlowResponse(
        @Path("id") peopleId: Int,
        @Query("hello") world: String?
    ): Flow<Person>

    @GET("people/{id}/")
    fun getPeopleByIdCallResponse(
        @Path("id") peopleId: Int
    ): Call<Person>

    @GET("people/{id}/")
    fun queryTest(
        @Path("id") peopleId: Int,
        @Query("hello") world: String?
    ): Call<Person>

    @GET("people/{id}/")
    suspend fun getPersonResponse(
        @Path("id") personId: Int
    ): Response<Person>
}
