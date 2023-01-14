

import com.example.api.JsonPlaceHolderApi
import com.example.model.MyOwnResponse
import com.example.model.commonKtorfit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun main() {

    GlobalScope.launch {
        println("Launch")

        val test =  commonKtorfit.create<JsonPlaceHolderApi>().getCommentsByPostIdResponse("3")

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

