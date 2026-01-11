package de.jensklingenberg.ktorfit

import de.jensklingenberg.ktorfit.http.GET
import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

annotation class AuthRequired(val optional: Boolean = false)

annotation class CustomHeader(val name: String, val value: String)

/**
 * Test API interface with annotations
 */
interface AnnotationsTestApi {
    @AuthRequired(optional = true)
    @CustomHeader("X-Test-Header", "test-value")
    @GET("posts")
    suspend fun getPosts(): String

    @AuthRequired(optional = false)
    @GET("users")
    suspend fun getUsers(): String
}

class AnnotationsIntegrationTest {
    @Test
    fun `when Ktorfit method has annotations, plugin can retrieve them via getOrNull`() =
        runTest {
            var annotationsFound: List<Any>? = null
            var annotationsViaExtension: List<Any>? = null

            // Create a plugin that intercepts requests and checks annotations
            val annotationsTestPlugin =
                createClientPlugin("AnnotationsTestPlugin") {
                    onRequest { request, _ ->
                        // Test retrieving annotations via getOrNull (as plugins would do)
                        annotationsFound = request.attributes.getOrNull(annotationsAttributeKey)

                        // Test retrieving annotations via extension property
                        annotationsViaExtension = request.annotations
                    }
                }

            val engine =
                object : TestEngine() {
                    override fun getRequestData(data: HttpRequestData) {
                        // Request was intercepted by plugin, annotations should be set
                    }
                }

            val ktorfit =
                Ktorfit.Builder().baseUrl("http://localhost/").httpClient(
                    HttpClient(engine) {
                        install(annotationsTestPlugin)
                    }
                ).build()

            ktorfit.createAnnotationsTestApi().getPosts()

            // Verify annotations were found by the plugin
            assertNotNull("Annotations should be retrievable via getOrNull in plugin", annotationsFound)
            assertTrue("Should have found annotations", annotationsFound?.isNotEmpty() == true)

            // Verify annotations can be retrieved via extension property
            assertNotNull("Annotations should be retrievable via extension property", annotationsViaExtension)
            assertEquals(annotationsFound, annotationsViaExtension)

            // Verify we can filter by annotation type
            val authAnnotations = annotationsFound?.filterIsInstance<AuthRequired>()
            assertTrue("Should be able to filter AuthRequired annotations", authAnnotations?.isNotEmpty() == true)
            assertEquals(true, authAnnotations?.first()?.optional)

            val headerAnnotations = annotationsFound?.filterIsInstance<CustomHeader>()
            assertTrue("Should be able to filter CustomHeader annotations", headerAnnotations?.isNotEmpty() == true)
            assertEquals("X-Test-Header", headerAnnotations?.first()?.name)
            assertEquals("test-value", headerAnnotations?.first()?.value)
        }

    @Test
    fun `when Ktorfit method has annotations, plugin can retrieve them and check values`() =
        runTest {
            var authAnnotation: AuthRequired? = null

            // Create a plugin that uses annotations (similar to real-world usage)
            val authPlugin =
                createClientPlugin("AuthPlugin") {
                    onRequest { request, _ ->
                        // This is how plugins would use annotations in real scenarios
                        val auth = request.annotations.filterIsInstance<AuthRequired>().firstOrNull()
                        authAnnotation = auth
                    }
                }

            val engine =
                object : TestEngine() {
                    override fun getRequestData(data: HttpRequestData) {
                        // Request intercepted
                    }
                }

            val ktorfit =
                Ktorfit.Builder().baseUrl("http://localhost/").httpClient(
                    HttpClient(engine) {
                        install(authPlugin)
                    }
                ).build()

            ktorfit.createAnnotationsTestApi().getPosts()

            assertNotNull("AuthRequired annotation should be found", authAnnotation)
            assertEquals(true, authAnnotation?.optional)

            // Reset and test with optional = false
            authAnnotation = null

            ktorfit.createAnnotationsTestApi().getUsers()

            assertNotNull("AuthRequired annotation should be found for getUsers", authAnnotation)
            assertEquals(false, authAnnotation?.optional)
        }

    @Test
    fun `annotationsAttributeKey uses the same instance in generated code and plugin`() =
        runTest {
            var retrievedViaGetOrNull: List<Any>? = null
            var retrievedViaExtension: List<Any>? = null

            val testPlugin =
                createClientPlugin("TestPlugin") {
                    onRequest { request, _ ->
                        // Verify both methods work and return the same data
                        retrievedViaGetOrNull = request.attributes.getOrNull(annotationsAttributeKey)
                        retrievedViaExtension = request.annotations
                    }
                }

            val engine =
                object : TestEngine() {
                    override fun getRequestData(data: HttpRequestData) {
                        // Request intercepted
                    }
                }

            val ktorfit =
                Ktorfit.Builder().baseUrl("http://localhost/").httpClient(
                    HttpClient(engine) {
                        install(testPlugin)
                    }
                ).build()

            ktorfit.createAnnotationsTestApi().getPosts()

            // Verify both retrieval methods work
            assertNotNull("Should retrieve annotations via getOrNull", retrievedViaGetOrNull)
            assertNotNull("Should retrieve annotations via extension property", retrievedViaExtension)

            // Verify they return the same data (same AttributeKey instance)
            assertEquals(retrievedViaGetOrNull, retrievedViaExtension)
            assertTrue("Annotations should not be empty", retrievedViaGetOrNull?.isNotEmpty() == true)
        }
}
