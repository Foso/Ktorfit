package de.jensklingenberg.androidonlyexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.jensklingenberg.androidonlyexample.ui.theme.AndroidOnlyExampleTheme
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.http.GET
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val ktorfit = Ktorfit("https://swapi.dev/api/", HttpClient {
    install(ContentNegotiation) {
        json(Json { isLenient = true; ignoreUnknownKeys = true })
    }
}).also {
    it.addResponseConverter(FlowResponseConverter())
}

class MainActivity : ComponentActivity() {

    val api: StarWarsApi = ktorfit.create<StarWarsApi>()

    val peopleState = MutableStateFlow(listOf<Person>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidOnlyExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val people = peopleState.rememberAsState()

                    LazyColumn {
                        items(people) { person ->
                            Text(person.name)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            peopleState.value = api.getPeople(page = 1)
        }
    }
}