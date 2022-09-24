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
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.builtin.FlowRequestConverter
import de.jensklingenberg.ktorfit.converter.builtin.FlowResponseConverter
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

val ktorfit = ktorfit {
    baseUrl("https://swapi.dev/api/")
    httpClient(HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
    })
    requestConverter(FlowRequestConverter()).build()
}
val api: StarWarsApi = ktorfit.create<StarWarsApi>()

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

        lifecycleScope.launch {
            peopleState.value = api.getPerson(1)
        }
    }
}