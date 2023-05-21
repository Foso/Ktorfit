package de.jensklingenberg.ktorfit.demo


import com.example.api.JsonPlaceHolderApi
import com.example.model.Comment
import com.example.model.StringToIntRequestConverterFactory
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.converter.builtin.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.builtin.CallResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.FlowConverterFactory
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
        //level = LogLevel.ALL
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
    responseConverter(CallResponseConverter())
}


fun main() {

    val api = jvmKtorfit.create<JsonPlaceHolderApi>()

    // val str = api.getPostById(3)

    val test = api.getCommentsByPostIdQuery(listOf("2", "3", "t t"))

    test?.onExecute(object :Callback<List<Comment>>{
        override fun onResponse(call: List<Comment>, response: HttpResponse) {
            println(call)
        }

        override fun onError(exception: Throwable) {
            exception
        }

    })
    runBlocking {


        delay(3000)
    }

}

