package de.jensklingenberg.ktorfit

import KtorfitProcessorProvider
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.*
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert
import org.junit.Test
import java.io.File

class StreamingAnnotationTest() {
    private val httpStatement = SourceFile.kotlin(
        "HttpStatement.kt", """
      package io.ktor.client.statement
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.ReqBuilder

interface HttpStatement
    """
    )

    @Test
    fun whenStreamingAnnotationFound_SetHttpStatementAsFunctionReturnType() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement

interface TestService {
 @Streaming
   @GET("posts")
   suspend fun getPostsStreaming(): HttpStatement
}
    """
        )


        val expectedFunctionText = """public override suspend fun getPostsStreaming(): HttpStatement {
    val _ext: HttpRequestBuilder.() -> Unit = {
        this.method = HttpMethod.parse("GET") 
        }
    val requestData = RequestData(relativeUrl="posts",
        returnTypeData = TypeData("io.ktor.client.statement.HttpStatement"),
        requestTypeInfo=typeInfo<HttpStatement>(),
        returnTypeInfo = typeInfo<HttpStatement>(),
        ktorfitRequestBuilder = _ext) 

    return ktorfitClient.suspendRequest<HttpStatement, HttpStatement>(requestData)!!
  }"""

        val compilation = KotlinCompilation().apply {
            sources = listOf(httpStatement, source)
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
        assertThat(generatedFile.readText().contains(expectedFunctionText)).isTrue()
    }

    @Test
    fun whenStreamingReturnTypeNotHttpStatement_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement

interface TestService {
 @Streaming
   @GET("posts")
   suspend fun getPostsStreaming(): String
}
    """
        )

        val compilation = KotlinCompilation().apply {
            sources = listOf(httpStatement, source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(KtorfitProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.FOR_STREAMING_THE_RETURN_TYPE_MUST_BE_HTTP_STATEMENT))
    }

}