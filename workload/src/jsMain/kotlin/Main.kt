

import com.example.api.GithubService
import com.example.model.commonKtorfit
import com.example.model.jsonPlaceHolderApi
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.adapter.FlowResponseConverter
import de.jensklingenberg.ktorfit.adapter.KtorfitCallResponseConverter
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import io.ktor.client.statement.*

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun main() {

    commonKtorfit.addResponseConverter(FlowResponseConverter())
    commonKtorfit.addResponseConverter(KtorfitCallResponseConverter())



    jsonPlaceHolderApi.callPosts().onExecute(object :Callback<String>{
        override fun onResponse(call: String, response: HttpResponse) {
            println("onResponse"+ call)

        }

        override fun onError(exception: Exception) {
            println("onError"+ exception)
        }

    })


    GlobalScope.launch {
        println("Launch")
        jsonPlaceHolderApi.getPosts().collect {
            println("HALLO"+it)
        }

        delay(3000)

    }


}

