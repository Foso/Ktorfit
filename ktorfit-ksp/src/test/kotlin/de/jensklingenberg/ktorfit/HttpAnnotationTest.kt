package de.jensklingenberg.ktorfit

import KtorfitProcessorProvider
import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Assert
import org.junit.Test
import java.io.File

class HttpAnnotationTest() {


    @Test
    fun testFunctionWithGET() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET

interface TestService {

    @GET("user")
    suspend fun test(): String
    
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
        assertThat(result.exitCode).isEqualTo(ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        assertThat(generatedFile.exists()).isTrue()
        assertThat(generatedFile.readText()).isEqualTo(testFunctionWithGETExpected)
    }

    @Test
    fun testFunctionWithGETAndPath() {

        val source = SourceFile.kotlin(
            "CustomCallable.kt", """
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
        relativeUrl="user/$\{client.encode(id)}",
        qualifiedRawTypeName="kotlin.String") 

    return client.suspendRequest<String, String>(requestData)
  }""".replace("\\{", "{")

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        assertThat(result.exitCode).isEqualTo(ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        assertThat(generatedFile.exists()).isTrue()
        assertThat(generatedFile.readText().contains(expectedFunctionText)).isTrue()
    }

    @Test
    fun whenMultipleHttpMethodsFound_throwError() {
        val source = SourceFile.kotlin("CustomCallable.kt", """
      package com.example.api

import com.example.model.github.GithubFollowerResponseItem
import com.example.model.github.Issuedata
import com.example.model.github.TestReeeItem
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

interface GithubService {

    @GET("user/followers")
    @POST("repos/foso/experimental/issues")
    suspend fun test(): String
    
}
    """)

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        assertThat(result.exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains("Ktorfit: Only one HTTP method is allowed. Found: GET, POST at test"))
    }
}