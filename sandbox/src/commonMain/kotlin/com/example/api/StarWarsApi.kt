package com.example.api

import com.example.model.People
import com.example.model.PeopleList
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface StarWarsApi {
    companion object {
        const val baseUrl = "https://swapi.dev/api/"
    }

    @GET("people/{id}/")
    suspend fun <T> getPersonById(
        @Path("id") peopleId: T,
    ): People

    @GET("people")
    suspend fun <T> getPoepl(): PeopleList

    @GET("people/{id}/")
    suspend fun test(
        @Query query: String
    ): String
}
