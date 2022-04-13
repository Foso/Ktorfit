package de.jensklingenberg.ktorfit.demo

import com.example.api.GithubService
import de.jensklingenberg.ktorfit.adapter.FlowResponseConverter
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.adapter.KtorfitCallResponseConverter
import de.jensklingenberg.ktorfit.create
import io.ktor.client.*
import io.ktor.client.plugins.*
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


    val jvmKtorfit = Ktorfit(baseUrl = GithubService.baseUrl, jvmClient)

    val testApi = jvmKtorfit.create<GithubService>()



    jvmKtorfit.addResponseConverter(FlowResponseConverter())
    jvmKtorfit.addResponseConverter(RxResponseConverter())
    jvmKtorfit.addResponseConverter(KtorfitCallResponseConverter())


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