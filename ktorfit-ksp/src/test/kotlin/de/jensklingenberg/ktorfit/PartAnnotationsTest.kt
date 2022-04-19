package de.jensklingenberg.ktorfit

import KtorfitProcessorProvider
import com.google.common.truth.Truth
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert
import org.junit.Test
import java.io.File

class PartAnnotationsTest {


    @Test
    fun whenNoPartAnnotationsFound_KeepPartsArgumentEmpty() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET

interface TestService {

    @GET("posts")
    suspend fun test(): String
    
}
    """
        )

        val expectedFunctionText = "parts ="

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
    fun whenPartAnnotationFoundAddItToPartsArgument() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Part

interface TestService {
   
    @POST("posts")
    suspend fun test(@Part("name") testPart: String)
}
    """
        )

        val expected = "parts = mapOf(\"name\" to testPart)"

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
    fun whenPartMapAnnotationFoundAddItToPartsArgument() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.PartMap
import de.jensklingenberg.ktorfit.http.POST

interface TestService {
    
    @POST("posts")
    suspend fun test(@PartMap() testPartMap: Map<String, String>)
    
}
    """
        )


        val expected = "parts = testPartMap"

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
    fun testFunctionWithPartAndPartMap() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PartMap
import de.jensklingenberg.ktorfit.http.Part

interface TestService {

    @POST("posts")
   fun example(@Part("name") testPart: String, @PartMap() name: Map<String, String>)
    
}
    """
        )


        val expected =  "parts = mapOf(\"name\" to testPart)+name"

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
    fun whenPartMapTypeIsNotMap_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
    import de.jensklingenberg.ktorfit.http.POST
    import de.jensklingenberg.ktorfit.http.PartMap

interface TestService {
   @POST("posts")
   fun example(@PartMap() name: String)
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
        Assert.assertTrue(result.messages.contains(KtorfitError.PART_MAP_PARAMETER_TYPE_MUST_BE_MAP))
    }

    @Test
    fun whenPartNullable_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
    import de.jensklingenberg.ktorfit.http.POST
    import de.jensklingenberg.ktorfit.http.Part

interface TestService {
   @POST("posts")
   fun example(@Part() name: String?)
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
        Assert.assertTrue(result.messages.contains(KtorfitError.PART_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE))
    }


}

