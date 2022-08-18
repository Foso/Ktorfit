import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.adapter.CoreResponseConverter
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
            val ktorfit = Ktorfit.Builder().baseUrl("www.example.com").build()
        } catch (illegal: IllegalStateException) {
            assert(illegal.message == "Base URL needs to end with /")
        }
    }

    @Test
    fun whenBaseUrlEmptyThrowError() {
        try {
            val ktorfit = Ktorfit.Builder().baseUrl("").build()
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

        val ktorfit = Ktorfit.Builder().baseUrl("www.test.de/").httpClient(HttpClient(engine)).build()
        runBlocking {
            val requestData = RequestData(
                method = "GET", relativeUrl = "posts", qualifiedRawTypeName = "kotlin.String"
            )
            KtorfitClient(ktorfit).suspendRequest<String, String>(requestData)
        }


    }

    @Test
    fun whenResponseConverterInvalidThrowError() {
        try {
            val ktorfit = Ktorfit.Builder().baseUrl("/").responseConverter(object : CoreResponseConverter {
                override fun supportedType(returnTypeName: String): Boolean {
                    return true
                }
            })
        } catch (illegal: IllegalArgumentException) {
            assert(illegal.message == "Your response converter must be either of type ResponseConverter or SuspendRespondConverter")
        }
    }
}

