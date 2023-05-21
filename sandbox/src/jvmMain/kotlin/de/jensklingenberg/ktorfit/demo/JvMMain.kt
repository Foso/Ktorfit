package de.jensklingenberg.ktorfit.demo


import com.example.UserFactory
import com.example.api.JsonPlaceHolderApi
import com.example.model.Comment
import com.example.model.ExampleApi
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.builtin.CallResponseConverter
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.internal.TypeData
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.reflect.full.createType
import kotlin.reflect.full.defaultType


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
    converterFactories(UserFactory())
}



fun main() {

Ktorfit.Builder().converterFactories(UserFactory()).baseUrl("foo").build()
    runBlocking {

       val user = userKtorfit.create<ExampleApi>().getUser()

        user?.let {
            println(user.name)
        }
        delay(3000)
    }

}

