package de.jensklingenberg.ktorfit.demo

import com.example.api.GithubService
import de.jensklingenberg.ktorfit.converter.builtin.FlowResponseConverter
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


fun main() {

    val jvmClient = HttpClient() {
        install(ContentNegotiation) {

            json(Json { isLenient = true; ignoreUnknownKeys = true; })
        }
        expectSuccess = false


    }


    val jvmKtorfit = ktorfit {
        baseUrl(GithubService.baseUrl)
        httpClient(jvmClient)
        responseConverter(
            FlowResponseConverter(),
            RxRequestConverter()
        )
    }



}