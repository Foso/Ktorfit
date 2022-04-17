package de.jensklingenberg.androidonlyexample

interface StarWarsApi {

    @GET("people/{id}/")
    suspend fun getPerson(@Path("id") personId: Int): Person

    @GET("people")
    fun getPeople(@Query("page") page: Int): List<Person>

    @GET("people")
    fun getPeopleFlow(@Query("page") page: Int): Flow<Person>

}