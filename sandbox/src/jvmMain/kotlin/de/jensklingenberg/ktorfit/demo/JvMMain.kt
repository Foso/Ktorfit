package de.jensklingenberg.ktorfit.demo


import com.example.api.StarWarsApi
import com.example.model.People
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.adapter.FlowResponseConverter
import de.jensklingenberg.ktorfit.adapter.KtorfitCallResponseConverter
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


val jvmClient = HttpClient {

    install(ContentNegotiation) {
        json(Json { isLenient = true; ignoreUnknownKeys = true })
    }
    this.developmentMode = true
    expectSuccess = false


}

interface JvmPlaceHolderApi : StarWarsApi {

    @GET("people/{id}/")
    suspend fun getPersonById2(@Path("id") peopleId: Int): People

    @GET("people/{id}/")
    suspend fun testQuery(@Path("id") peopleId: Int, @Query("huhu") name: Array<String?>): People

    @Streaming
    @GET("people/1/")
    suspend fun getPostsStreaming(): HttpStatement
}

val jvmKtorfit = Ktorfit(baseUrl = StarWarsApi.baseUrl, jvmClient)


fun main() {

    jvmKtorfit.addResponseConverter(FlowResponseConverter())
    jvmKtorfit.addResponseConverter(RxResponseConverter())
    jvmKtorfit.addResponseConverter(KtorfitCallResponseConverter())


    val exampleApi = jvmKtorfit.create<JvmPlaceHolderApi>()





    println("==============================================")
    runBlocking {
        val response = exampleApi.getPersonById2(1)

        println("LI    " + response)


        delay(3000)
    }

}
