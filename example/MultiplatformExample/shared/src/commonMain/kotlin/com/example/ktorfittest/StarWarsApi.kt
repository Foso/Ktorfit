package com.example.ktorfittest

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

interface StarWarsApi {
    @GET("people/{id}/")
    suspend fun getPersonByIdResponse(@Path("id") peopleId: Int): String

    @GET("people/{id}/")
    fun getPersonByIdFlowResponse(@Path("id") peopleId: Int): Flow<String>

    @GET("people/{id}/")
    fun getPeopleByIdFlowResponse(@Path("id") peopleId: Int): Flow<People>
}