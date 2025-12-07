package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError.HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING
import de.jensklingenberg.ktorfit.model.KtorfitError.HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class HeaderMapAnnotationsTest {
    @Test
    fun whenNullableHeaderMapWithStringValueAnnotationFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

interface TestService {
    @GET("posts")
    suspend fun test(@HeaderMap("testHeader") testParameter: Map<String,String>?): String
}
    """,
            )

        val expectedHeadersArgumentText =
            "headers{\n" +
                "        testParameter?.forEach{ append(it.key , it.value)}\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

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
    fun whenHeaderMapWithNullableStringValueAnnotationFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

interface TestService {
    @GET("posts")
    suspend fun test(@HeaderMap("testHeader") testParameter: Map<String,String?>): String
}
    """,
            )

        val expectedHeadersArgumentText =
            "headers{\n" +
                "        testParameter.forEach{it.value?.let{ value ->  append(it.key , value) }}\n" +
                "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

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
    fun whenHeaderMapTypeIsNotMap_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

interface TestService {
    @GET("posts")
    suspend fun test(@HeaderMap("testHeader") testParameter: String): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        assertTrue(result.messages.contains(HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP))
    }

    @Test
    fun whenHeaderMapKeysIsNotString_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

interface TestService {

    @GET("posts")
    suspend fun test(@HeaderMap("testHeader") testParameter: Map<Int,String>): String
    
}
    """,
            )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING))
    }
}
