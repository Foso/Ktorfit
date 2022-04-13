package com.example.ktorfittest

import de.jensklingenberg.ktorfit.adapter.FlowResponseConverter
import de.jensklingenberg.ktorfit.create
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.native.concurrent.ThreadLocal

val ktorfit = de.jensklingenberg.ktorfit.Ktorfit("https://swapi.dev/api/", HttpClient {
    install(ContentNegotiation) {
        json(Json { isLenient = true; ignoreUnknownKeys = true })
    }
}).also {
    it.addResponseConverter(FlowResponseConverter())
}


val starWarsApi = ktorfit.create<StarWarsApi>()

class Greeting {
    fun greeting(): String {

        loadData()
        return "Hello, ${Platform().platform}!"
    }


}

fun loadData() {
    GlobalScope.launch {
        val response = starWarsApi.getPersonByIdResponse(3)
        println(Platform().platform + ":" + response)
    }
}