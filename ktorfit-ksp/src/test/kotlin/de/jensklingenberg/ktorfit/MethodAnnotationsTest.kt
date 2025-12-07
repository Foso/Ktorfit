package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MethodAnnotationsTest {
    @Test
    fun `always add function annotations as 'annotation' attribute`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Tag

annotation class Test1(value: String = "Foo")
annotation class Test2(value1: String, value2: String = "Bar")

interface TestService {
    @Test1
    @Test1("Bar")
    @Test2("Foo")
    @GET("posts")
    suspend fun test(): String
}
    """,
            )

        val expectedHeadersArgumentText =
            """attributes.put(AttributeKey("__ktorfit_attribute_annotations"), listOf(
        Test1(`value` = "Foo"),
        Test1(`value` = "Bar"),
        Test2(value1 = "Foo", value2 = "Bar"),
        ))"""

        val compilation = getCompilation(listOf(source))
        println(compilation.languageVersion)

        val generatedSourcesDir = compilation.apply { compile() }.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        println(actualSource)
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun `when function annotation includes 'OptIn' annotation we skip it`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Tag
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

annotation class Test1

@OptIn(ExperimentalCompilerApi::class)
interface TestService {
    @Test1
    @OptIn(ExperimentalCompilerApi::class)
    @GET("posts")
    suspend fun test(): String
}
    """,
            )

        val expectedHeadersArgumentText =
            """attributes.put(AttributeKey("__ktorfit_attribute_annotations"), listOf(
        Test1(),
        ))"""

        val compilation = getCompilation(listOf(source))
        println(compilation.languageVersion)

        val generatedSourcesDir = compilation.apply { compile() }.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun `when function annotation have parameter named 'method' don't clash with http method parse`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
                        package com.example.api
        
                        import de.jensklingenberg.ktorfit.http.GET
                        import de.jensklingenberg.ktorfit.http.Query
        
                        interface TestService {
                            @GET("posts")
                            suspend fun test(@Query("method") method: String, @Query url: String): String
                        }
                        """,
            )
        val expectedHttpMethodParsedText = """this.method = HttpMethod.parse("GET")"""
        val expectedQueryMethod = """method?.let{ parameter("method", "$/it") }""".replace("$/", "$")

        val compilation = getCompilation(listOf(source))
        println(compilation.languageVersion)

        val generatedSourcesDir = compilation.apply { compile() }.kspSourcesDir
        val generatedFile = File(generatedSourcesDir, "/kotlin/com/example/api/_TestServiceImpl.kt")

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHttpMethodParsedText))
        assertTrue(actualSource.contains(expectedQueryMethod))
    }
}
