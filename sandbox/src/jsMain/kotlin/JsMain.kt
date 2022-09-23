

import com.example.model.Post
import com.example.model.jsonPlaceHolderApi
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.statement.*

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun main() {





    GlobalScope.launch {
        println("Launch")
        jsonPlaceHolderApi.getPosts().collect {
            println("HALLO"+it)
        }

        delay(3000)

    }


}

