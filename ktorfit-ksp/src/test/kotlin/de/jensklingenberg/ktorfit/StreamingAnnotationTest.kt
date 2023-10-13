package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class StreamingAnnotationTest {
    private val httpStatement = SourceFile.kotlin(
        "HttpStatement.kt", """
      package io.ktor.client.statement
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.ReqBuilder

interface HttpStatement
    """
    )

    @Test
    fun whenStreamingAnnotationFound_ThenSetHttpStatementAsFunctionReturnType() {

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


        val expectedFunctionText = """return ktorfitClient.suspendRequest<HttpStatement, HttpStatement>(__typeData,__ext)!!"""

        val compilation = getCompilation(listOf(httpStatement, source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)


        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        assertTrue(generatedFile.exists())

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFunctionText))
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
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR,result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.FOR_STREAMING_THE_RETURN_TYPE_MUST_BE_HTTP_STATEMENT))
    }

}