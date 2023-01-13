package de.jensklingenberg.ktorfit.demo


import com.example.api.JsonPlaceHolderApi
import com.example.api.*
import de.jensklingenberg.ktorfit.converter.builtin.CallResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.FlowResponseConverter

import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
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
    baseUrl(JsonPlaceHolderApi.baseUrl)
    httpClient(jvmClient)
    responseConverter(
        FlowResponseConverter(),
        RxRequestConverter(),
        CallResponseConverter(),
    )

}




internal class Test2() {
    val api2 = jvmKtorfit.create<JsonPlaceHolderApi>()

}

fun main() {
val tes = "dd3sh 332"
    val api2 = Test2().api2

    runBlocking {



        val test = api2.deletePosts(3)




        delay(3000)
    }

}
