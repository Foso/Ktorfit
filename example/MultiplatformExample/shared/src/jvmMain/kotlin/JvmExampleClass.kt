import com.example.ktorfittest.JvmStarWarsApi
import com.example.ktorfittest.ktorfit
import com.example.ktorfittest.starWarsApi
import de.jensklingenberg.ktorfit.create
import kotlinx.coroutines.runBlocking


fun main() {
    runBlocking {
        val response = starWarsApi.getPersonByIdResponse(3)
        println(response)


    }
}