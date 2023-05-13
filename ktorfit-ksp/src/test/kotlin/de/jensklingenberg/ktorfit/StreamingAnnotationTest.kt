package de.jensklingenberg.ktorfit

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
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


        val expectedFunctionText = """val _ext: HttpRequestBuilder.() -> Unit = {
        method = HttpMethod.parse("GET")
        url(ktorfitClient.baseUrl + "posts") 
        }"""

        val compilation = getCompilation(listOf(httpStatement, source))
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        val actualSource = generatedFile.readText()
        assertThat(actualSource.contains(expectedFunctionText)).isTrue()
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

        val compilation = getCompilation(listOf(httpStatement, source))
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.FOR_STREAMING_THE_RETURN_TYPE_MUST_BE_HTTP_STATEMENT))
    }

}