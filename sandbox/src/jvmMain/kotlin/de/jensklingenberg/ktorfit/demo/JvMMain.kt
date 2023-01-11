package de.jensklingenberg.ktorfit.demo


import com.example.api.GithubService
import com.example.api.JsonPlaceHolderApi
import com.example.model.Jens
import com.example.model.MyOwnResponse
import com.example.model.MyOwnResponseConverter
import com.example.model.StringToIntRequestConverter
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
import ktorfit.Test


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



fun zop(zup:()->Unit){

}

fun main() {


    runBlocking {

         val api2 = jvmKtorfit.create<JsonPlaceHolderApi>()


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
