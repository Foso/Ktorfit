package de.jensklingenberg.ktorfit.demo


import com.example.api.JsonPlaceHolderApi
import com.example.model.Comment
import com.example.model.MyOwnResponseConverter
import com.example.model.StringToIntRequestConverter
import com.example.model.StringToIntRequestConverter2
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.builtin.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.builtin.CallResponseConverter
import de.jensklingenberg.ktorfit.converter.builtin.FlowConverterFactory
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
import kotlinx.coroutines.flow.collectLatest
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
    requestConverter(
        StringToIntRequestConverter(),
        StringToIntRequestConverter2()
    )
    converterFactories(
        CallConverterFactory(),
        FlowConverterFactory(),
        CommentFactory()
    )
}


fun main() {

    val api = jvmKtorfit.create<JsonPlaceHolderApi>()

    // val str = api.getPostById(3)

    val test = api.getCommentsByPostIdQuery(listOf("2", "3", "t t"))

    test.onExecute(object :Callback<Comment>{
        override fun onResponse(call: Comment, response: HttpResponse) {
            println(call)
        }

        override fun onError(exception: Throwable) {
            exception
        }

    })
    runBlocking {

        val test2 = api.getPostById(3)

        test2?.let {
            it
        }

        delay(3000)
    }

}


class CommentFactory : Converter.Factory {

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        return if (typeData.typeInfo.type == Comment::class) {
            object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(response: HttpResponse): Any {
                    val data = response.body<List<Comment>>(typeInfo<List<Comment>>())
                    return data.first()
                }
            }
        } else {
            null
        }

    }
}