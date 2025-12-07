package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class HttpAnnotationTest {
    @Test
    fun testFunctionWithGET() {
        val expectedSource = """method = HttpMethod.parse("GET")"""

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
        assertTrue(actualSource.contains(expectedSource))
    }

    @Test
    fun testCustomHttpMethod() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """     
package com.example.api
import de.jensklingenberg.ktorfit.http.HTTP
import de.jensklingenberg.ktorfit.http.Body

interface TestService {

    @HTTP("CUSTOM","user")
    suspend fun test(): String
    
}""",
            )

        val expectedSource = """method = HttpMethod.parse("CUSTOM")"""

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedSource))
    }

    @Test
    fun testCustomHttpMethodWithBody() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """     
package com.example.api
import de.jensklingenberg.ktorfit.http.HTTP
import de.jensklingenberg.ktorfit.http.Body

interface TestService {

    @HTTP("GET2","user",true)
    suspend fun test(@Body body: String): String
    
}""",
            )

        val expectedFunctionText = """val _ext: HttpRequestBuilder.() -> Unit = {
        this.method = HttpMethod.parse("GET2")
        url{
        takeFrom(_baseUrl + "user")
        }
        setBody(body) 
        }"""

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
    fun whenMultipleHttpMethodsFound_throwError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
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
    """,
            )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.ONLY_ONE_HTTP_METHOD_IS_ALLOWED))
    }
}
