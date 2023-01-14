import com.example.api.JsonPlaceHolderApi
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.builtin.FlowResponseConverter

import io.ktor.client.*
import kotlinx.coroutines.runBlocking

fun main() {

    val linuxKtorfit = Ktorfit.Builder().baseUrl(JsonPlaceHolderApi.baseUrl).httpClient(HttpClient())
        .responseConverter(FlowResponseConverter()).build()

    val api = linuxKtorfit.create<JsonPlaceHolderApi>()
    runBlocking {
        api.getPosts().collect {
            println(it)
        }
    }

    println("ddd")
}
