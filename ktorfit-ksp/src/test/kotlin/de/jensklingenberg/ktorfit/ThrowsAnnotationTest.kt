package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ThrowsAnnotationTest {
    @Test
    fun `when Throws annotation is used it should not be added to the generated implementation`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import kotlin.jvm.Throws

interface TestService {
    @GET("posts")
    @Throws(Exception::class)
    suspend fun test(): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val generatedSourcesDir = compilation.apply { compile() }.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()

        // The @Throws annotation should NOT appear in the generated code
        assertFalse("@Throws annotation should be filtered out", actualSource.contains("@Throws"))

        // The function should still be generated
        assertTrue("Generated function should exist", actualSource.contains("override suspend fun test()"))
    }

    @Test
    fun `when multiple Throws annotations are used none should be added to the generated implementation`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import kotlin.jvm.Throws
import java.io.IOException

interface TestService {
    @GET("posts")
    @Throws(IOException::class, IllegalArgumentException::class)
    suspend fun test1(): String
    
    @POST("posts")
    @Throws(Exception::class)
    suspend fun test2(): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val generatedSourcesDir = compilation.apply { compile() }.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()

        // The @Throws annotations should NOT appear in the generated code
        assertFalse("@Throws annotation should be filtered out", actualSource.contains("@Throws"))

        // The functions should still be generated
        assertTrue("Generated function test1 should exist", actualSource.contains("override suspend fun test1()"))
        assertTrue("Generated function test2 should exist", actualSource.contains("override suspend fun test2()"))
    }


    @Test
    fun `when custom annotation ending with Throws is used it should be filtered out`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomThrows

interface TestService {
    @GET("posts")
    @CustomThrows
    suspend fun test(): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val generatedSourcesDir = compilation.apply { compile() }.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()

        // CustomThrows should be filtered out since it ends with "Throws"
        assertTrue("Custom annotation ending with Throws should be filtered out", actualSource.contains("@CustomThrows"))

        // The function should still be generated
        assertTrue("Generated function should exist", actualSource.contains("override suspend fun test()"))
    }

    @Test
    fun `when kotlin stdlib annotations are used they should be filtered out`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import kotlin.Deprecated
import kotlin.jvm.Throws

interface TestService {
    @GET("posts")
    @Deprecated("Use test2 instead")
    @Throws(Exception::class)
    suspend fun test(): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val generatedSourcesDir = compilation.apply { compile() }.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()

        // Kotlin stdlib annotations should be filtered out
        assertFalse("@Deprecated annotation should be filtered out", actualSource.contains("@Deprecated"))
        assertFalse("@Throws annotation should be filtered out", actualSource.contains("@Throws"))

        // The function should still be generated
        assertTrue("Generated function should exist", actualSource.contains("override suspend fun test()"))
    }

    @Test
    fun `when non-kotlin custom annotations are used they should be preserved`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import kotlin.jvm.Throws

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomAnnotation

interface TestService {
    @GET("posts")
    @CustomAnnotation
    @Throws(Exception::class)
    suspend fun test(): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val generatedSourcesDir = compilation.apply { compile() }.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()

        // Custom annotation should be preserved (as an attribute)
        assertTrue("Custom annotation should be preserved as attribute", actualSource.contains("CustomAnnotation"))

        // But @Throws should still be filtered out
        assertFalse("@Throws annotation should be filtered out", actualSource.contains("@Throws"))

        // The function should still be generated
        assertTrue("Generated function should exist", actualSource.contains("override suspend fun test()"))
    }
}

