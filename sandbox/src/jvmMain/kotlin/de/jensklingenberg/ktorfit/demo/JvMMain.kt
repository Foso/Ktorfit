package de.jensklingenberg.ktorfit.demo

import com.example.UserFactory
import com.example.api.JsonPlaceHolderApi
import com.example.api.KtorSamplesApi
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
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.*
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.io.readBytes

val jvmClient =
    HttpClient {

        install(Logging) {
             level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Ktor Client: $message")
                    }
                }
        }

        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }

        this.developmentMode = true
        expectSuccess = false
    }

val jvmKtorfit =
    ktorfit {
        baseUrl(JsonPlaceHolderApi.baseUrl)
        httpClient(jvmClient)
    }

val userKtorfit =
    ktorfit {
        baseUrl(KtorSamplesApi.baseUrl)
        httpClient(jvmClient)

        converterFactories(
            FlowConverterFactory(),
            MyOwnResponseConverterFactory(),
            UserFactory(),
            CallConverterFactory()
        )
    }

val api: KtorSamplesApi = userKtorfit.create<KtorSamplesApi>()

fun main() {
    runBlocking {
        val user = api.uploadFile("test", formData {

            append("description", "Ktor logo")
            append("image", File("/Users/jens.klingenberg/Code/2025/Ktorfit/ktor_logo.png").readBytes(), Headers.build {
                append(HttpHeaders.ContentType, "image/png")
                append(HttpHeaders.ContentDisposition, "filename=\"ktor_logo.png\"")
            })

        }, mapOf())


        delay(3000)
    }
}
