import com.example.model.Comment
import com.example.model.MyOwnResponse
import com.example.model.jsonPlaceHolderApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    @OptIn(DelicateCoroutinesApi::class)
    GlobalScope.launch {
        println("Launch")

        when (val test = jsonPlaceHolderApi.getCommentsByPostIdResponse("3")) {
            is MyOwnResponse.Success -> {
                val list = test.data
                println(list.size)
            }

            else -> {
                val error = (test as MyOwnResponse.Error<*>)
                println(error.ex)
            }
        }

        delay(3000)
    }
}
