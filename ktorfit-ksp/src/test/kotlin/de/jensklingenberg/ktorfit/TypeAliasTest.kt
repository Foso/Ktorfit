package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class TypeAliasTest {
    private val httpReqBuilderSource =
        SourceFile.kotlin(
            "ReqBuilder.kt",
            """
        package io.ktor.client.request

interface HttpRequestBuilder
        """,
        )

    @Test
    fun whenHeaderMapUsesTypeAlias_ShouldWorkCorrectly() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

typealias StringMap = Map<String, String>

interface TestService {
    @GET("posts")
    suspend fun test(@HeaderMap headers: StringMap): String
}
    """,
            )

        val expectedHeadersArgumentText = "headers.forEach{ append(it.key , \"\${it.value}\")"

        val compilation = getCompilation(listOf(source))
        compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        assertTrue("Generated file should exist", generatedFile.exists())
        val actualSource = generatedFile.readText()
        assertTrue("Generated source should contain expected headers code", actualSource.contains(expectedHeadersArgumentText))
    }

    @Test
    fun whenQueryMapUsesTypeAlias_ShouldWorkCorrectly() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

typealias QueryParams = Map<String, String>

interface TestService {
    @GET("posts")
    suspend fun test(@QueryMap params: QueryParams): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))
        compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        // Test passes if file was generated without type validation errors
        assertTrue("Generated file should exist - typealias was resolved correctly", generatedFile.exists())
    }

    @Test
    fun whenFieldMapUsesTypeAlias_ShouldWorkCorrectly() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.FormUrlEncoded

typealias FieldParams = Map<String, String>

interface TestService {
    @FormUrlEncoded
    @POST("posts")
    suspend fun test(@FieldMap fields: FieldParams): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))
        compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        // Test passes if file was generated without type validation errors
        assertTrue("Generated file should exist - typealias was resolved correctly", generatedFile.exists())
    }

    @Test
    fun whenPartMapUsesTypeAlias_ShouldWorkCorrectly() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PartMap
import de.jensklingenberg.ktorfit.http.Multipart

typealias Parts = Map<String, String>

interface TestService {
    @Multipart
    @POST("upload")
    suspend fun test(@PartMap parts: Parts): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))
        compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        // Test passes if file was generated without type validation errors
        assertTrue("Generated file should exist - typealias was resolved correctly", generatedFile.exists())
    }

    @Test
    fun whenRequestBuilderUsesTypeAlias_ShouldWorkCorrectly() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.ReqBuilder
import io.ktor.client.request.HttpRequestBuilder

typealias RequestConfig = HttpRequestBuilder.() -> Unit

interface TestService {
    @GET("posts")
    suspend fun test(@ReqBuilder builder: RequestConfig): String
}
    """,
            )

        val compilation = getCompilation(listOf(httpReqBuilderSource, source))
        val result = compilation.compile()

        // Compilation will fail due to missing Ktorfit imports, but KSP should not produce the type error
        assertFalse(
            "Should not contain HttpRequestBuilder type error for typealias",
            result.messages.contains(KtorfitError.REQ_BUILDER_PARAMETER_TYPE_NEEDS_TO_BE_HTTP_REQUEST_BUILDER)
        )

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        // Test passes if file was generated without type validation errors
        assertTrue("Generated file should exist - typealias for HttpRequestBuilder was resolved correctly", generatedFile.exists())
    }

    @Test
    fun whenNestedTypeAliasUsed_ShouldResolveToBaseType() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap

typealias StringStringMap = Map<String, String>
typealias Headers = StringStringMap

interface TestService {
    @GET("posts")
    suspend fun test(@HeaderMap headers: Headers): String
}
    """,
            )

        val expectedHeadersArgumentText = "headers.forEach{ append(it.key , \"\${it.value}\")"

        val compilation = getCompilation(listOf(source))
        compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        assertTrue(generatedFile.exists())
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }
}
