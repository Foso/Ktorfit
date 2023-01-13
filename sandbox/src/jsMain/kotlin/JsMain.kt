

import com.example.model.jsonPlaceHolderApi
import io.ktor.client.statement.*

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun main() {





    GlobalScope.launch {
        println("Launch")

        val test = jsonPlaceHolderApi.getCommentsByPostIdResponse("3")

        when (test) {
            is MyOwnResponse.Success -> {
                println(test)
            }

            else -> {
                println(test)
            }
        }

        delay(3000)

    }


}
