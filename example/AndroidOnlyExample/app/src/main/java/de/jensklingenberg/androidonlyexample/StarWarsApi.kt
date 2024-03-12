package de.jensklingenberg.androidonlyexample

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
    suspend fun getPerson(@Path("id") personId: Int): Person

    @GET("people")
    fun getPeopleFlow(@Query("page") page: Int): Flow<Person>

    @GET("people/{id}/")
    fun getPersonCall(@Path("id") personId: Int): Call<Person>

    @GET("people/{id}/")
    suspend fun getPersonResponse(@Path("id") personId: Int): Response<Person>

}