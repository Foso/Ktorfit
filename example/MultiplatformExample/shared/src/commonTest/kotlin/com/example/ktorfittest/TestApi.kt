package com.example.ktorfittest

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestApi {
    companion object {
        const val baseUrl = "https://swapi.dev/api/"
    }

    @GET("people/{id}/")
    suspend fun getPersonByIdResponse(@Path("id") peopleId: Int): Person
}

