package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class KtorfitOptionsTest {
    @Test
    fun `when QualifiedType options not set then don't generate qualifiedTypeName`() {
        val expected =
            "qualifiedTypename =\n" +
                    "        \"kotlin.collections.List<kotlin.Triple<kotlin.String,kotlin.Int?,kotlin.String>>\")"
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

class Test<T>

interface TestService {
@GET("posts")
suspend fun test(): List<Triple<String,Int?,String>>
}
    """,
            )

        val source2 =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api2
    """,
            )

        val compilation = getCompilation(listOf(source2, source), mutableMapOf())
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        val actualSource = generatedFile.readText()
        assertFalse(actualSource.contains(expected))
    }

    @Test
    fun `when QualifiedType options is set then generate qualifiedTypeName`() {
        val expected = "qualifiedTypename"
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

class Test<T>

interface TestService {
@GET("posts")
suspend fun test(): List<Triple<String,Int?,String>>
}
    """,
            )

        val source2 =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api2
    """,
            )

        val compilation = getCompilation(listOf(source2, source), mutableMapOf("Ktorfit_QualifiedTypeName" to "true"))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expected))
    }
}
