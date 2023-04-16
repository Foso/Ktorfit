import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.date.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class TestEngine() : HttpClientEngineBase("ktor-mock") {
    override val config: HttpClientEngineConfig
        get() = HttpClientEngineConfig()
    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.Default

    private suspend fun HttpClientEngine.createCallContext(parentJob: Job): CoroutineContext {
        val callJob = Job(parentJob)
        val callContext = coroutineContext + callJob

        return callContext
    }

    @InternalAPI
    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        val coroutineContext1 = createCallContext(data.executionContext)
        getRequestData(data)
        return HttpResponseData(
            HttpStatusCode.Accepted,
            GMTDate(),
            Headers.Empty,
            HttpProtocolVersion.HTTP_2_0,
            "",
            coroutineContext1
        )
    }

    open fun getRequestData(data: HttpRequestData) {

    }

}

