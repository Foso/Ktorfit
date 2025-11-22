package com.example.api

import com.example.model.People
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

//@TypeConverters(MyConv::class)
interface StarWarsApi {
    companion object {
        const val baseUrl = "https://swapi.dev/api/"
    }

    @Suppress("ArrayInDataClass")
    @GET("people/{id}/")
    fun getPersonById(
        @Path("id") peopleId: Int
    ): Call<People>

    @GET("people/stormtrooper/all")
    fun subscribeToStormtroopers(): Flow<List<People>>

    @GET("people/stormtrooper/all")
    suspend fun summonStormtroopers(): List<People>
}

