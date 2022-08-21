package com.example.model

import com.example.api.JsonPlaceHolderApi
import de.jensklingenberg.ktorfit.converter.builtin.FlowResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.KtorfitCallResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.KtorfitSuspendCallResponseConverter
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*


val httpClient2 = HttpClient() {

}

val commonKtorfit = ktorfit {
    baseUrl(JsonPlaceHolderApi.baseUrl)
    httpClient(httpClient2)
    responseConverter(
        KtorfitCallResponseConverter(),
        KtorfitSuspendCallResponseConverter(),
        FlowResponseConverter()
    )
}


val jsonPlaceHolderApi = commonKtorfit.create<JsonPlaceHolderApi>()

