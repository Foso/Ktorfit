
import com.example.api.StarWarsApi
import com.example.model.People
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.adapter.FlowResponseConverter
import de.jensklingenberg.ktorfit.create
import io.ktor.client.*
import io.ktor.client.plugins.*

import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

fun main() {


    val httpClient2 = HttpClient() {
            install(ContentNegotiation){
                json(Json { isLenient = true; ignoreUnknownKeys = true })
            }

    }
    val cli2 = Ktorfit(baseUrl = "https://jsonplaceholder.typicode.com/").apply {
        httpClient = httpClient2
    }

    cli2.addResponseConverter(FlowResponseConverter())

    val secondApi3 = cli2.create<StarWarsApi>()

   runBlocking {
        secondApi3.getPersonById(3).onExecute(object :Callback<People>{
            override fun onResponse(call: People, response: HttpResponse) {
                println(call)
            }

            override fun onError(exception: Exception) {


            }

        })

    }
}
