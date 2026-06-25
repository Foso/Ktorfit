package com.example

import com.example.api.createStarWarsApi
import com.example.model.People
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SummonStormtroopersTest {
    @Test
    fun `Given mocked response When subscribeToStormtroopers Then obtain not null expected result`() = runTest {
        // Given
        val stringJsonListStormtroopersContent = """
                    [
                        {
                            "gender": "male"
                        },
                        {
                            "gender": "female"
                        },
                        {
                            "gender": "twilek"
                        }
                    ]
                """.trimIndent()
        val mockEngine = MockEngine {
            respond(
                content = stringJsonListStormtroopersContent,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val starWarsApi = setUpTestKtorfit(mockEngine).createStarWarsApi()

        // When
        val result = starWarsApi.subscribeToStormtroopers()

        // Then
        assertNotNull(result)
        assertEquals(Json.decodeFromString<List<People>>(stringJsonListStormtroopersContent), result.first())
    }

    @Test
    fun `Given mocked response When summonStormtroopers Then obtain not null expected result`() = runTest {
        // Given
        val stringJsonListStormtroopersContent = """
                    [
                        {
                            "gender": "male"
                        },
                        {
                            "gender": "female"
                        },
                        {
                            "gender": "twilek"
                        }
                    ]
                """.trimIndent()
        val mockEngine = MockEngine {
            respond(
                content = stringJsonListStormtroopersContent,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val starWarsApi = setUpTestKtorfit(mockEngine).createStarWarsApi()

        // When
        val result = starWarsApi.summonStormtroopers()

        // Then
        assertNotNull(result)
        assertEquals(Json.decodeFromString<List<People>>(stringJsonListStormtroopersContent), result)
    }
}

private fun setUpTestKtorfit(engine: HttpClientEngine): Ktorfit {
    val httpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                }
            )
        }
    }
    return Ktorfit.Builder()
        .converterFactories(FlowConverterFactory())
        .httpClient(httpClient)
        .build()
}