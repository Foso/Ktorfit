package de.jensklingenberg.ktorfit.demo


import com.example.api.JsonPlaceHolderApi
import com.example.model.MyOwnResponse
import com.example.model.MyOwnResponseConverter
import com.example.model.StringToIntRequestConverter
import de.jensklingenberg.ktorfit.Ktorfit
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
        MyOwnResponseConverter()
    )
    requestConverter(
        StringToIntRequestConverter()
    )
}




internal class Test2() {


}

fun main() {
val tes = "dd33 32"
    val api2: JsonPlaceHolderApi = jvmKtorfit.create<JsonPlaceHolderApi>()

    runBlocking {



        val test = api2.getCommentsByPostIdResponse("3")

        when (test) {
            is MyOwnResponse.Success -> {
                test
            }

            else -> {
                test
            }
        }


        delay(3000)
    }

}
