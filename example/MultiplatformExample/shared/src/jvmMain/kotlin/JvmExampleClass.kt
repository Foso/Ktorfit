import com.example.ktorfittest.Person
import com.example.ktorfittest.starWarsApi
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking


fun main() {

    starWarsApi.getPeopleByIdCallResponse(3).onExecute(object : Callback<Person> {
        override fun onError(exception: Throwable) {
            exception
        }

        override fun onResponse(call: Person, response: HttpResponse) {
            println("onResponse" + call)
        }

    })

    runBlocking {
        val response = starWarsApi.getPersonByIdResponse(3)
        println(response)

    }


}