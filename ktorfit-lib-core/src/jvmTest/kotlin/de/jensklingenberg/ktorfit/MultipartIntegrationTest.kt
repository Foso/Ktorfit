package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Part
import de.jensklingenberg.ktorfit.http.PartMap
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

typealias MultipartUploadParts = List<PartData?>

interface MultipartIntegrationTestApi {
    @Multipart
    @POST("upload")
    suspend fun upload(
        @Part("description") description: String,
        @Part("count") count: Int,
        @Part("flags") flags: List<Boolean>,
        @PartMap metadata: Map<String, String>,
        @Part("") files: List<PartData>,
    ): String

    @Multipart
    @POST("upload")
    suspend fun uploadLists(
        @Part("first") first: MultipartUploadParts,
        @Part("second") second: List<PartData>,
    ): String

    @Multipart
    @POST("upload")
    suspend fun uploadArray(
        @Part files: Array<PartData?>,
    ): String
}

class MultipartIntegrationTest {
    @Test
    fun upload_preservesFileBytesAndHeadersAlongsideTextParts() =
        runTest {
            // Given
            val files = fileParts("image", "image.png")
            val textParts =
                formData {
                    append("description", "Ktor logo")
                    append("count", "2")
                    append("flags", "true")
                    append("flags", "false")
                    append("category", "avatar")
                }

            // When / Then
            assertMultipartRequest(textParts + files) { api ->
                api.upload("Ktor logo", 2, listOf(true, false), mapOf("category" to "avatar"), files)
            }
        }

    @Test
    fun uploadLists_preservesMultipleFileListsAndSkipsNullElements() =
        runTest {
            // Given
            val first = fileParts("front", "front.png")
            val second = fileParts("back", "back.png")

            // When / Then
            assertMultipartRequest(first + second) { api ->
                api.uploadLists(first + null, second)
            }
        }

    @Test
    fun uploadArray_preservesFileBytesAndHeaders() =
        runTest {
            // Given
            val files = fileParts("image", "image.png")

            // When / Then
            assertMultipartRequest(files) { api ->
                api.uploadArray(arrayOf(files.single(), null))
            }
        }

    @Test
    fun upload_withEmptyFileListSendsTextParts() =
        runTest {
            // Given
            val textParts =
                formData {
                    append("description", "No attachment")
                    append("count", "0")
                }

            // When / Then
            assertMultipartRequest(textParts) { api ->
                api.upload("No attachment", 0, emptyList(), emptyMap(), emptyList())
            }
        }

    private fun fileParts(
        name: String,
        filename: String,
    ): List<PartData> =
        formData {
            append(
                name,
                byteArrayOf(0, 1, 127, -128, -1),
                Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                    append("X-Part-Header", "preserved")
                },
            )
        }

    private suspend fun assertMultipartRequest(
        expectedParts: List<PartData>,
        upload: suspend (MultipartIntegrationTestApi) -> String,
    ) {
        var requests = 0
        val engine =
            MockEngine { request ->
                requests++
                val actual = request.body as MultiPartFormDataContent
                val boundary = requireNotNull(actual.contentType.parameter("boundary"))
                val expected = MultiPartFormDataContent(expectedParts, boundary)
                assertArrayEquals(expected.toByteArray(), actual.toByteArray())
                respond("ok")
            }

        HttpClient(engine).use { client ->
            val api =
                Ktorfit
                    .Builder()
                    .baseUrl("https://example.com/")
                    .httpClient(client)
                    .build()
                    .createMultipartIntegrationTestApi()

            assertEquals("ok", upload(api))
            assertEquals(1, requests)
        }
    }
}
