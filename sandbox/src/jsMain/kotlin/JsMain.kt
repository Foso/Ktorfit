

import com.example.model.Comment
import com.example.model.MyOwnResponse
import com.example.model.jsonPlaceHolderApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun main() {

    GlobalScope.launch {
        println("Launch")

        when (val test = jsonPlaceHolderApi.getCommentsByPostIdResponse("3")) {
            is MyOwnResponse.Success -> {
               val list =  test.data as List<Comment>
                println(list.size)
            }

            else -> {
                val error = (test as MyOwnResponse.Error)
                println(error.ex)
            }
        }

        delay(3000)

    }


}

