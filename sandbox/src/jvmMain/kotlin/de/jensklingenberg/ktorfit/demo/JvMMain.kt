package de.jensklingenberg.ktorfit.demo


import com.example.UserFactory
import com.example.api.*
import com.example.model.ExampleApi
import com.example.model.MyOwnResponseConverterFactory
import com.example.model.createExampleApi
import de.jensklingenberg.ktorfit.Event
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.converter.MyWebSocketFactory
import de.jensklingenberg.ktorfit.internal.*
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


val jvmClient = HttpClient {

    install(Logging) {
        //level = LogLevel.ALL
    }

    install(WebSockets) {
        pingInterval = 20_000
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
        CallConverterFactory(),
        MyWebSocketFactory()
    )
}

val api: ExampleApi = userKtorfit.createExampleApi()


@OptIn(InternalKtorfitApi::class)
fun main() {

    val test =
        KtorfitConverterHelper(Ktorfit.Builder().converterFactories(MyWebSocketFactory()).httpClient(jvmClient).build())

    val dies = Ktorfit.Builder().baseUrl("ws://localhost:23567/", false).converterFactories(MyWebSocketFactory()).httpClient(jvmClient).build()

    val websocket = dies.createQueryTestApi().testQueryWithEncoded("test","showdown")

    GlobalScope.launch {
        websocket.events.onEach {
            when (it) {
                is Event.Close -> {
                    println("Closed hier" + it.message)
                }

                Event.Created -> {

                }

                is Event.Error -> {
                    println("Error hier" + it.message)
                }

                is Event.Message -> {

                }

                Event.Opened -> {
                    println("Opened hier")
                }
            }
        }.collect {}
    }

    GlobalScope.launch {

        websocket.open()
    }

    GlobalScope.launch {
        delay(1000)
        websocket.send("Hello   ")
    }

    runBlocking {

        delay(9000)
        //websocket.close()
    }
}

