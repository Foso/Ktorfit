package de.jensklingenberg.ktorfit.demo


import com.example.UserFactory
import com.example.api.JsonPlaceHolderApi
import com.example.model.ExampleApi
import com.example.model.MyOwnResponse
import com.example.model.MyOwnResponseConverterFactory
import com.example.model.createExampleApi
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


val jvmClient = HttpClient {

    install(Logging) {
        //level = LogLevel.ALL
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

}


val userKtorfit = ktorfit {
    baseUrl("https://foso.github.io/Ktorfit/")
    httpClient(jvmClient)

    converterFactories(
        FlowConverterFactory(),
        MyOwnResponseConverterFactory(),
        UserFactory(),
        CallConverterFactory()
    )
}

val api : ExampleApi = userKtorfit.createExampleApi()

fun main() {

    runBlocking {

        val user = api.getUserResponse()

        when (user) {
            is MyOwnResponse.Success -> {
                println(user.data)
            }

            is MyOwnResponse.Error<*> -> {
                println(user.ex)
            }
        }
        delay(3000)
    }

}

