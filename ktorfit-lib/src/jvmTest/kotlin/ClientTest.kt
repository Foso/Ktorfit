import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import de.jensklingenberg.ktorfit.internal.RequestData
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ClientTest {

    @Test
    fun whenBaseUrlNotEndingWithSlashThrowError() {
        try {
            val ktorfit = Ktorfit("www.example.com")
        } catch (illegal: IllegalStateException) {
            assert(illegal.message == "Base URL needs to end with /")
        }
    }

    @Test
    fun whenBaseUrlEmptyThrowError() {
        try {
            val ktorfit = Ktorfit("")
        } catch (illegal: IllegalStateException) {
            assert(illegal.message == "Base URL required")
        }

    }

    @Test
    fun checkIfCorrectHttpMethodIsSet() {

        val engine = object : TestEngine(){
            override fun getRequestData(data: HttpRequestData) {
                Assert.assertEquals("GET", data.method.value)
            }
        }

        val ktorfit = Ktorfit("www.test.de/", HttpClient(engine))
        runBlocking {
            val requestData = RequestData(
                method = "GET", relativeUrl = "posts", qualifiedRawTypeName = "kotlin.String"
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }


    }
}

