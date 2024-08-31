import com.example.api.JsonPlaceHolderApi
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking

fun main() {
    val linuxKtorfit =
        Ktorfit
            .Builder()
            .baseUrl(JsonPlaceHolderApi.baseUrl)
            .httpClient(HttpClient())
            .converterFactories(FlowConverterFactory())
            .build()

    val api = linuxKtorfit.create<JsonPlaceHolderApi>()
    runBlocking {
        api.getPosts().collect {
            println(it)
        }
    }

    println("ddd")
}
