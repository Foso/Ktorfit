package de.jensklingenberg.androidonlyexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import de.jensklingenberg.androidonlyexample.ui.theme.AndroidOnlyExampleTheme
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.converter.ResponseConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MyCustomException(message: String) : Exception(message)

val ktorfit = ktorfit {

    baseUrl(StarWarsApi.baseUrl)
    httpClient(HttpClient {
        HttpResponseValidator {
            validateResponse { response ->
                throw MyCustomException("My Custom Exception")
            }
        }
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
    })
    converterFactories(
        FlowConverterFactory(),
        CallConverterFactory(),
        ResponseConverterFactory()
    )

}
val api = ktorfit.createStarWarsApi()

class MainActivity : ComponentActivity() {


    private val peopleState = mutableStateOf<Person?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidOnlyExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    peopleState.value?.let {
                        Text(it.name ?: "")
                    }

                }
            }
        }

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }

        lifecycleScope.launch(exceptionHandler) {
            try {
                peopleState.value = api.getPerson(1)
            } catch (e: MyCustomException) {
                throw e
            }
        }


    }
}