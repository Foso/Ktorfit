package com.example.model

import com.example.api.JsonPlaceHolderApi
import de.jensklingenberg.ktorfit.adapter.FlowResponseConverter
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.adapter.KtorfitCallResponseConverter
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


val httpClient2 = HttpClient() {
    install(ContentNegotiation) {
        json(Json { isLenient = true; ignoreUnknownKeys = true })
    }
}


val commonKtorfit = Ktorfit(baseUrl = JsonPlaceHolderApi.baseUrl, httpClient2).apply {
    addResponseConverter(KtorfitCallResponseConverter())
    addResponseConverter(FlowResponseConverter())
}


val jsonPlaceHolderApi = commonKtorfit.create<JsonPlaceHolderApi>()

