package de.jensklingenberg.ktorfit.demo

import com.example.api.GithubService
import de.jensklingenberg.ktorfit.converter.builtin.FlowRequestConverter
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


fun main() {

    val jvmClient = HttpClient() {
        install(ContentNegotiation) {

            json(Json { isLenient = true; ignoreUnknownKeys = true; })
        }
        expectSuccess = false


    }


    val jvmKtorfit = ktorfit {
        baseUrl(GithubService.baseUrl)
        httpClient(jvmClient)
        requestConverter(
            FlowRequestConverter(),
            RxRequestConverter()
        )
    }

    val testApi = jvmKtorfit.create<GithubService>()


    runBlocking {

        testApi.listCommits("foso","Experimental").collect{
            println(it.first().author)
        }


     //  println( testApi.createIsseu(Issuedata("hey","ho")))
//BODY {"title":"title","body":"This is a test"}
       // BODY Issuedata(title=Hallo, body=hhhh)
        delay(3000)

    }

}