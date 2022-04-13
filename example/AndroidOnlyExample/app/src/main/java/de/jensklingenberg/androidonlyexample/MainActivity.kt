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

interface ExampleApi {
    @GET("people/1/")
    suspend fun getPerson(): String

    @GET("people/1/")
    fun getPersonCall(): Call<String>
}
val ktorfit = Ktorfit(baseUrl = "https://swapi.dev/api/")

fun testApi() = ktorfit.create<ExampleApi>()
class TestClass {
    init {
        val exampleApi = ktorfit.create<ExampleApi>()
        GlobalScope.launch {
            Log.d("Android:",exampleApi.getPerson())
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestClass()
            TestJava().test()
            AndroidOnlyExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidOnlyExampleTheme {
        Greeting("Android")
    }
}