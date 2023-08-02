package de.jensklingenberg.ktorfit.demo


import com.example.UserFactory
import com.example.api.JsonPlaceHolderApi
import com.example.model.ExampleApi
import de.jensklingenberg.ktorfit.converter.builtin.CallResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.FlowConverterFactory
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



val userKtorfit = ktorfit {
    baseUrl("https://foso.github.io/Ktorfit/")
    httpClient(jvmClient)

    converterFactories(FlowConverterFactory(),UserFactory())
}



fun main() {

    runBlocking {

       val user = userKtorfit.create<ExampleApi>().getUser()

        if(user.isSuccessful){
            println(user.body())
        }else{
            user.errorBody()
        }
        delay(3000)
    }

}

