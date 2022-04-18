package de.jensklingenberg.ktorfit

import KtorfitProcessorProvider
import com.google.common.truth.Truth
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.TypeData
import de.jensklingenberg.ktorfit.model.annotations.Header
import de.jensklingenberg.ktorfit.requestData.getHeadersArgumentText
import org.junit.Assert
import org.junit.Test
import java.io.File

class HeaderAnnotationsTest {


    @Test
    fun whenNoHeaderAnnotationsFound_KeepHeadersArgumentEmpty() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestService {

    @GET("posts")
    suspend fun test(): String
    
}
    """
        )


        val expectedFunctionText = "headers ="

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expectedFunctionText)).isFalse()
    }



    @Test
    fun whenHeadersAnnotationFound_AddHeader() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers

interface TestService {

@Headers(value = ["x:y","a:b"])
    @GET("posts")
    suspend fun test(): String
    
}
    """
        )


        val expected ="headers = listOf(HeaderData(\"x\",\"y\"), HeaderData(\"a\",\"b\"))"

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expected)).isTrue()
    }


    @Test
    fun whenHeaderAnnotationFound_AddHeader() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface TestService {

    @GET("posts")
    suspend fun test(@Header("testHeader") testParameter: String): String
    
}
    """
        )


        val expected ="headers = listOf(HeaderData(\"testHeader\",testParameter))"

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expected)).isTrue()
    }

    @Test
    fun whenHeaderMapAnnotationFound_AddHeader() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

interface TestService {

    @GET("posts")
    suspend fun test(@HeaderMap("testHeader") testParameter: Map<String,String>): String
    
}
    """
        )


        val expected = "headers = listOf(HeaderData(\"\",testParameter))"

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expected)).isTrue()
    }

    @Test
    fun whenHeaderMapTypeIsNotMap_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

interface TestService {

    @GET("posts")
    suspend fun test(@HeaderMap("testHeader") testParameter: String): String
    
}
    """
        )

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP))
    }

    @Test
    fun whenHeaderMapKeysIsNotString_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

interface TestService {

    @GET("posts")
    suspend fun test(@HeaderMap("testHeader") testParameter: Map<Int,String>): String
    
}
    """
        )

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING))
    }



}

