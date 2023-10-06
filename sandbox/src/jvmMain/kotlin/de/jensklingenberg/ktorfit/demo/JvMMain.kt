package de.jensklingenberg.ktorfit.demo


import com.example.UserFactory
import com.example.api.JsonPlaceHolderApi
import com.example.model.ExampleApi
import com.example.model.User
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.converter.builtin.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.builtin.FlowConverterFactory
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

    converterFactories(FlowConverterFactory(), UserFactory(), CallConverterFactory())
}


fun main() {

    runBlocking {

        val user = userKtorfit.create<ExampleApi>().getUser()

        if (user.isSuccessful) {
            println(user.body())
        } else {
            user.errorBody()
            println(user.failure().toString())
        }
        delay(3000)
    }

}

