package com.example.ktorfittest


import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

val ktorfit = ktorfit {
    baseUrl(StarWarsApi.baseUrl)
    httpClient(HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
    })
    converterFactories(
        FlowConverterFactory(),
        CallConverterFactory()
    )
}


val starWarsApi = ktorfit.createStarWarsApi()

class Greeting {
    fun greeting(): String {

        loadData()
        return "Hello, ${Platform().platform}! Look in the LogCat"
    }


}

@OptIn(DelicateCoroutinesApi::class)
fun loadData() {
    GlobalScope.launch {
        val response = starWarsApi.getPersonByIdResponse(3)
        println("Ktorfit:" + Platform().platform + ":" + response)
    }
}