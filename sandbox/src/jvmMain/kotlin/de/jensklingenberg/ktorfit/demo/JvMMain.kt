package de.jensklingenberg.ktorfit.demo


import com.example.api.JsonPlaceHolderApi
import com.example.model.MyOwnResponseConverter
import com.example.model.StringToIntRequestConverter
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.converter.builtin.CallResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.FlowResponseConverter
import de.jensklingenberg.ktorfit.internal.TypeData
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass


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


fun main() {


    runBlocking {

        val api = jvmKtorfit.create<JsonPlaceHolderApi>()

        val test = api.getCommentsByPostIdQuery(listOf("2","3","t t"))

        test.onExecute(object :Callback<String>{
            override fun onResponse(call: String, response: HttpResponse) {
                call
            }

            override fun onError(exception: Throwable) {
                exception
            }

        })


        delay(3000)
    }

}
