package com.example.model

import com.example.api.JsonPlaceHolderApi
import de.jensklingenberg.ktorfit.converter.builtin.CallResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.FlowRequestConverter
import de.jensklingenberg.ktorfit.converter.builtin.CallRequestConverter
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


val commonClient = HttpClient() {
    install(ContentNegotiation) {
         json(Json { isLenient = true; ignoreUnknownKeys = true })
    }
}

val commonKtorfit = ktorfit {
    baseUrl(JsonPlaceHolderApi.baseUrl)
    httpClient(commonClient)
    requestConverter(
        CallRequestConverter(),
        FlowRequestConverter()
    )
    responseConverter(CallResponseConverter())
}


val jsonPlaceHolderApi = commonKtorfit.create<JsonPlaceHolderApi>()

