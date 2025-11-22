package de.jensklingenberg.androidonlyexample

import de.jensklingenberg.ktorfit.core.TypeConverters
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Tag
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@TypeConverters(MyConverter::class)
interface StarWarsApi {

    companion object {
        const val baseUrl = "https://swapi.info/api/"
    }

    @GET("people/{id}/")
    suspend fun getPerson(@Path("id") personId: Int): Person

    @GET("people/{id}/")
    //@Deprecated("Just for testing")
    fun getPeopleFlow(@Path("id") page: Int, @Tag("myTag") test : Int = 3): Flow<Person>
}

class MyConverter{

    fun convert( getResponse: suspend () -> HttpResponse): Flow<Person>{
        return flow {
            val response = getResponse()
            val person= response.body<Person>()
            emit(person)
        }
    }
}