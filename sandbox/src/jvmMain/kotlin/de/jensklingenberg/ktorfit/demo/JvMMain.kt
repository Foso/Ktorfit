package de.jensklingenberg.ktorfit.demo


import com.example.api.JsonPlaceHolderApi
import com.example.model.Comment
import com.example.model.MyOwnResponse
import com.example.model.MyOwnResponseConverter
import com.example.model.jsonPlaceHolderApi
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.converter.builtin.CallResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.FlowRequestConverter
import de.jensklingenberg.ktorfit.converter.builtin.CallRequestConverter
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.statement.*
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
    requestConverter(
        FlowRequestConverter(),
        RxRequestConverter(),
        CallRequestConverter()
    )
    responseConverter(CallResponseConverter(), MyOwnResponseConverter())
}


fun main() {


    runBlocking {

     val api =   jvmKtorfit.create<JsonPlaceHolderApi>()


       val test = api.getCommentsByPostIdResponse(3)

        when(test){
            is MyOwnResponse.Success -> {
                test
            }
            else->{
                test
            }
        }


        delay(3000)
    }

}
