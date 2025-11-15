package com.example.api

import com.example.model.People
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.ReturnType
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.flow.Flow

interface StarWarsApi {
    companion object {
        const val baseUrl = "https://swapi.dev/api/"
    }

    @GET("people/{id}/"+ baseUrl)
    fun getPersonById(
        @Path("id") peopleId: Int
    ): Call<People>

    @GET("people/{id}/"+ baseUrl)
    fun <T : People, S> getPersonById23(
        @Path("id") peopleId: S,
        @ReturnType typeInfo: TypeInfo
    ): T

    @GET("people/stormtrooper/all")
    fun subscribeToStormtroopers(): Flow<List<People>>

    @GET("people/stormtrooper/all")
    suspend fun summonStormtroopers(): List<People>
}
