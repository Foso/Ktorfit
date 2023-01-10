import com.example.api.JsonPlaceHolderApi
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.builtin.FlowResponseConverter
import de.jensklingenberg.ktorfit.create
import io.ktor.client.*
import kotlinx.coroutines.runBlocking

fun main() {

    val linuxKtorfit = Ktorfit.Builder().baseUrl(JsonPlaceHolderApi.baseUrl).httpClient(HttpClient())
        .responseConverter(FlowResponseConverter()).build()



    println("ddd")
}
