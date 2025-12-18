package de.jensklingenberg.ktorfit

import io.ktor.client.request.HttpRequestBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Simple data class to simulate annotation instances
 * (In real usage, these would be actual annotation instances from KSP/KotlinPoet)
 */
data class TestAnnotation(val value: String = "default")

class AnnotationsTest {
    @Test
    fun `when annotations are set, they can be retrieved via extension property`() {
        val builder = HttpRequestBuilder()
        val annotations = listOf(TestAnnotation("test-value"))

        // Simulate what generated code does: set annotations using annotationsAttributeKey
        builder.attributes.put(annotationsAttributeKey, annotations)

        // Verify annotations can be retrieved via extension property
        val retrievedAnnotations = builder.annotations
        assertEquals(1, retrievedAnnotations.size)
        assertEquals(annotations, retrievedAnnotations)
    }

    @Test
    fun `when annotations are set, they can be retrieved via getOrNull`() {
        val builder = HttpRequestBuilder()
        val annotations = listOf(TestAnnotation("test-value"))

        // Simulate what generated code does: set annotations using annotationsAttributeKey
        builder.attributes.put(annotationsAttributeKey, annotations)

        // Verify annotations can be retrieved via getOrNull (as plugins would do)
        val retrievedAnnotations = builder.attributes.getOrNull(annotationsAttributeKey)
        assertNotNull(retrievedAnnotations)
        assertEquals(annotations, retrievedAnnotations)
    }

    @Test
    fun `when no annotations are set, extension property returns empty list`() {
        val builder = HttpRequestBuilder()

        // Verify extension property returns empty list when no annotations are set
        val retrievedAnnotations = builder.annotations
        assertEquals(emptyList(), retrievedAnnotations)
    }

    @Test
    fun `when no annotations are set, getOrNull returns null`() {
        val builder = HttpRequestBuilder()

        // Verify getOrNull returns null when no annotations are set
        val retrievedAnnotations = builder.attributes.getOrNull(annotationsAttributeKey)
        assertNull(retrievedAnnotations)
    }

    @Test
    fun `when annotations are set, they can be filtered by type`() {
        val builder = HttpRequestBuilder()
        val annotations =
            listOf(
                TestAnnotation("value1"),
                TestAnnotation("value2"),
            )

        builder.attributes.put(annotationsAttributeKey, annotations)

        // Verify annotations can be filtered by type (as plugins would do)
        val testAnnotations = builder.annotations.filterIsInstance<TestAnnotation>()
        assertEquals(2, testAnnotations.size)
        assertEquals("value1", testAnnotations[0].value)
        assertEquals("value2", testAnnotations[1].value)
    }

    @Test
    fun `annotationsAttributeKey uses the same instance for setting and getting`() {
        val builder1 = HttpRequestBuilder()
        val builder2 = HttpRequestBuilder()
        val annotations = listOf(TestAnnotation("test"))

        // Set annotations on both builders using the same annotationsAttributeKey
        builder1.attributes.put(annotationsAttributeKey, annotations)
        builder2.attributes.put(annotationsAttributeKey, annotations)

        // Verify both can retrieve using the same key
        assertNotNull(builder1.attributes.getOrNull(annotationsAttributeKey))
        assertNotNull(builder2.attributes.getOrNull(annotationsAttributeKey))

        // Verify the extension property works on both
        assertEquals(annotations, builder1.annotations)
        assertEquals(annotations, builder2.annotations)
    }

    @Test
    fun `extension property and getOrNull return the same data`() {
        val builder = HttpRequestBuilder()
        val annotations = listOf(TestAnnotation("test"))

        builder.attributes.put(annotationsAttributeKey, annotations)

        // Verify both retrieval methods return the same data
        val viaExtension = builder.annotations
        val viaGetOrNull = builder.attributes.getOrNull(annotationsAttributeKey)

        assertNotNull(viaGetOrNull)
        assertEquals(viaExtension, viaGetOrNull)
    }
}
