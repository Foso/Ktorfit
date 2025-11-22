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
import de.jensklingenberg.androidonlyexample.StarWarsApi.Companion.baseUrl
import de.jensklingenberg.androidonlyexample.ui.theme.AndroidOnlyExampleTheme
import de.jensklingenberg.androidonlyexample._StarWarsApiImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json { isLenient = true; ignoreUnknownKeys = true })
    }
}

val api = _StarWarsApiImpl(baseUrl, httpClient)

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
             api.getPeopleFlow(1).collect {
                print(it.name)
            }
        }
    }
}