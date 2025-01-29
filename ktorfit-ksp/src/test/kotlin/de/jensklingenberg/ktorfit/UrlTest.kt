package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class UrlTest {
    @Test
    fun testFunctionWithGET() {
        val expectedFunctionSource = """url{
        takeFrom(_ktorfit.baseUrl + "user")
        }"""

        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET

interface TestService {
    @GET("user")
    suspend fun test(): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFunctionSource))
    }

    @Test
    fun testFunctionWithGETAndPath() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestService {
    @GET("user/{id}")
    suspend fun test(@Path("id") userId: String): String
}
    """,
            )

        val expectedFunctionText =
            """url{
        takeFrom(_ktorfit.baseUrl + "user/$\{"$\userId".encodeURLPath()}")
        } """.replace("$\\", "$")

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFunctionText))
    }

    @Test
    fun testFunctionWithGETAndPathWithDefault() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestService {

    @GET("user/{userId}")
    suspend fun test(@Path userId: String): String
    
}
    """,
            )

        val expectedFunctionText =
            """url{
        takeFrom(_ktorfit.baseUrl + "user/$\{"$\userId".encodeURLPath()}")
        } """.replace("$\\", "$")

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFunctionText))
    }

    @Test
    fun testFunctionWithGETAndUrlAnno() {
        val expectedFunctionSource =
            """url{
        takeFrom((_ktorfit.baseUrl.takeIf{ !url.startsWith("http")} ?: "") + "$\{url}")
        }""".replace("$\\", "$")

        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Url

interface TestService {
    @GET("")
    suspend fun test(@Url url: String): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFunctionSource))
    }

    // Multiple
    @Test
    fun whenHttpMethodAnnotationPathEmptyAndNoUrlAnno_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET

interface TestService {
    @GET("")
    suspend fun test(): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.missingEitherKeywordUrlOrUrlParameter("GET")))
    }

    @Test
    fun whenMultipleParameterWithUrlAnnotation_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Url

interface TestService {
    @GET("")
    suspend fun test(@Url url: String, @Url url2: String): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.MULTIPLE_URL_METHOD_ANNOTATIONS_FOUND))
    }

    @Test
    fun testFunctionWithGETAndAlreadyEncodedPath() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestService {
    @GET("user/{id}")
    suspend fun test(@Path("id",true) id: String): String
}
    """,
            )

        val expectedFunctionText =
            """url{
        takeFrom(_ktorfit.baseUrl + "user/$\{"$\id"}")
        }""".replace("$\\", "$")

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedFunctionText))
    }

    @Test
    fun whenPathParameterNullable_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestService {

    @GET("user/{id}")
    suspend fun test(@Path("id") id: String?): String
    
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.PATH_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE))
    }

    @Test
    fun whenPathUsedWithEmptyHttpAnnotationValue_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface TestService {

    @GET("")
    suspend fun test(@Path("id") id: String): String
    
}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.PATH_CAN_ONLY_BE_USED_WITH_RELATIVE_URL_ON))
    }
}
