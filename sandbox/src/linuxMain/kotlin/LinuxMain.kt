import com.example.api.JsonPlaceHolderApi
import de.jensklingenberg.ktorfit.ExperimentalFactoryRegistry
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalFactoryRegistry::class)
fun main() {
    val linuxKtorfit =
        Ktorfit
            .Builder()
            .baseUrl(JsonPlaceHolderApi.baseUrl)
            .httpClient(HttpClient())
            .converterFactories(FlowConverterFactory())
            .build()

    val api = linuxKtorfit.createUsingRegistry<JsonPlaceHolderApi>()
    runBlocking {
        api.getPosts().collect {
            println(it)
        }
    }

    println("ddd")
}
