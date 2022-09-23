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

class PathAnnotationsTest {

    @Test
    fun testFunctionWithGETAndPath() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestService {

    @GET("user/{id}")
    suspend fun test(@Path("id") id: String): String
    
}
    """
        )


        val expectedFunctionText = """public override suspend fun test(id: String): String {
    val requestData = RequestData(method="GET",
        relativeUrl="user/{id}",
        returnTypeData=TypeData("kotlin.String"),
        paths = listOf(PathData("id","äid",false))) 

    return client.suspendRequest<String, String>(requestData)!!
  }""".replace("\\{", "{").replace("ä","$")

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
        Truth.assertThat(generatedFile.readText().contains(expectedFunctionText)).isTrue()
    }

    @Test
    fun testFunctionWithGETAndAlreadyEncodedPath() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestService {

    @GET("user/{id}")
    suspend fun test(@Path("id",true) id: String): String
    
}
    """
        )


        val expectedFunctionText = """public override suspend fun test(id: String): String {
    val requestData = RequestData(method="GET",
        relativeUrl="user/{id}",
        returnTypeData=TypeData("kotlin.String"),
        paths = listOf(PathData("id","äid",true))) 

    return client.suspendRequest<String, String>(requestData)!!
  }""".replace("%", "").replace("ä","$")

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
        Truth.assertThat(generatedFile.readText().contains(expectedFunctionText)).isTrue()
    }

    @Test
    fun whenPathParameterNullable_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestService {

    @GET("user/{id}")
    suspend fun test(@Path("id") id: String?): String
    
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
        Assert.assertTrue(result.messages.contains(KtorfitError.PATH_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE))
    }

    @Test
    fun whenPathUsedWithEmptyHttpAnnotationValue_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestService {

    @GET("")
    suspend fun test(@Path("id") id: String): String
    
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
        Assert.assertTrue(result.messages.contains(KtorfitError.PATH_CAN_ONLY_BE_USED_WITH_RELATIVE_URL_ON))
    }
}

