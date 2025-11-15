package com.example.model

import com.example.api.JsonPlaceHolderApi
import com.example.api.createJsonPlaceHolderApi
import com.example.api.createStarWarsApi
import com.example.api.getPersonById23
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val commonClient =
    HttpClient {

        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

val commonKtorfit =
    ktorfit {
        baseUrl(JsonPlaceHolderApi.baseUrl)
        httpClient(commonClient)
        converterFactories(
            CallConverterFactory(),
            StringToIntRequestConverterFactory(),
            MyOwnResponseConverterFactory()
        )
    }

val jsonPlaceHolderApi = commonKtorfit.createJsonPlaceHolderApi()
 val  test = commonKtorfit.createStarWarsApi().getPersonById23<People, String>("dd")
