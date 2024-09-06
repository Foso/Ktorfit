package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class InheritanceTest {
    @Test
    fun `when Interface Without Ktorfit Annotations Is Extended Then dont add delegation`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.HeaderMap

interface SuperTestService<T>{
    suspend fun test(): T
}

interface TestService : SuperTestService<String> {
    @GET("posts")
    override suspend fun test(): T

    @GET("posts")
    suspend fun test(): String
}
    """,
            )

        val expectedHeadersArgumentText =
            "public class _TestServiceImpl(\n" +
                "  private val _ktorfit: Ktorfit,\n" +
                ") : TestService {"

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

    @Test
    fun `when Interface with Ktorfit Annotations Is Extended Then add delegation`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.HeaderMap

interface SuperTestService{
    @GET("posts")
    suspend fun test(): String
}

interface TestService : SuperTestService {
    @GET("posts")
    override suspend fun test(): T

    @GET("posts")
    suspend fun test(): String
}
    """,
            )

        val expectedHeadersArgumentText =
            "public class _TestServiceImpl(\n" +
                "  private val _ktorfit: Ktorfit,\n" +
                ") : TestService,\n" +
                "    SuperTestService by com.example.api._SuperTestServiceImpl(_ktorfit) {"

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
