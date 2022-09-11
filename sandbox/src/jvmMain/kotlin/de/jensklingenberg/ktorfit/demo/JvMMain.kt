package de.jensklingenberg.ktorfit.demo


import com.example.api.StarWarsApi
import de.jensklingenberg.ktorfit.converter.builtin.FlowResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.KtorfitCallResponseConverter
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


val jvmClient = HttpClient {

    install(Logging) {
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json(Json { isLenient = true; ignoreUnknownKeys = true })
    }
    this.developmentMode = true
    expectSuccess = false


}

val jvmKtorfit = ktorfit {
    baseUrl(StarWarsApi.baseUrl)
    httpClient(jvmClient)
    responseConverter(
        FlowResponseConverter(),
        RxResponseConverter(),
        KtorfitCallResponseConverter(),
        SuspendConverter()
    )
}


fun main() {
    val exampleApi = jvmKtorfit.create<JvmPlaceHolderApi>()

    println("==============================================")
    runBlocking {
        val response = exampleApi.testQuery(4, listOf("a",null,"c"),null)


        println("LI    " + response)


        delay(3000)
    }

}
