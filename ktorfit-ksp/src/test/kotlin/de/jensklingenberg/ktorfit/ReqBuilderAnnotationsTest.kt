package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReqBuilderAnnotationsTest {
    private val httpReqBuilderSource =
        SourceFile.kotlin(
            "ReqBuilder.kt",
            """
      package io.ktor.client.request
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.ReqBuilder

interface HttpRequestBuilder
    """,
        )

    @Test
    fun whenNoRequestBuilderAnnotationsFound_KeepArgumentEmpty() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET

interface TestService {

    @GET("posts")
    suspend fun test(): String
    
}
    """,
            )

        val expectedRequestBuilderArgumentText = "requestBuilder ="

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()


        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())
        assertFalse(generatedFile.readText().contains(expectedRequestBuilderArgumentText))
    }

    @Test
    fun addRequestBuilderArgumentWhenAnnotationFound() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.ReqBuilder
import io.ktor.client.request.*

interface TestService {
    @GET("posts")
    suspend fun test(@ReqBuilder builder : HttpRequestBuilder.() -> Unit)
}
    """,
            )

        val expectedRequestBuilderArgumentText = "builder(this)"

        val compilation = getCompilation(listOf(httpReqBuilderSource, source))
        val result = compilation.compile()


        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())

        assertTrue(generatedFile.readText().contains(expectedRequestBuilderArgumentText))
    }

    @Test
    fun whenMultipleReqBuilderFound_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
   package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.ReqBuilder
import io.ktor.client.request.*

interface TestService {

    @GET("posts")
    suspend fun test(@ReqBuilder builder : HttpRequestBuilder.() -> Unit,@ReqBuilder builder2 : HttpRequestBuilder.() -> Unit)
    
}
    """,
            )

        val compilation = getCompilation(listOf(httpReqBuilderSource, source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.ONLY_ONE_REQUEST_BUILDER_IS_ALLOWED))
    }

    @Test
    fun whenReqBuilderNOtReqBuilder_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
   package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.ReqBuilder
import io.ktor.client.request.*

interface TestService {

    @GET("posts")
    suspend fun test(@ReqBuilder builder : String)
    
}
    """,
            )

        val compilation = getCompilation(listOf(httpReqBuilderSource, source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.REQ_BUILDER_PARAMETER_TYPE_NEEDS_TO_BE_HTTP_REQUEST_BUILDER))
    }
}
