package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class TagAnnotationsTest {
    @Test
    fun whenTagsAnnotationFound_AddThemAsAttributeKey() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Tag

interface TestService {
    @GET("posts")
    suspend fun test(@Tag myTag1 : String, @Tag("myTag2") someParameter: Int?): String
}
    """,
            )

        val expectedHeadersArgumentText =
            """attributes.put(io.ktor.util.AttributeKey("myTag1"), myTag1)
        someParameter?.let{ attributes.put(io.ktor.util.AttributeKey("myTag2"), it) } """

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }
}
