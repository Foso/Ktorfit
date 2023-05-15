package de.jensklingenberg.ktorfit

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert
import org.junit.Test
import java.io.File

class HttpAnnotationTest {

    @Test
    fun testFunctionWithGET() {
        val expectedSource = """ method = HttpMethod.parse("GET")"""

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET

interface TestService {

    @GET("user")
    suspend fun test(): String
    
}
    """
        )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertThat(result.exitCode).isEqualTo(ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        assertThat(generatedFile.exists()).isTrue()
        val actualSource = generatedFile.readText()
        Assert.assertEquals(true, actualSource.contains(expectedSource))
    }

    @Test
    fun testCustomHttpMethod() {

        val source = SourceFile.kotlin(
            "Source.kt", """     
package com.example.api
import de.jensklingenberg.ktorfit.http.HTTP
import de.jensklingenberg.ktorfit.http.Body

interface TestService {

    @HTTP("CUSTOM","user")
    suspend fun test(): String
    
}"""
        )


        val expectedSource = """method = HttpMethod.parse("CUSTOM")"""

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        val actualSource = generatedFile.readText()
        Assert.assertEquals(true, actualSource.contains(expectedSource))
    }

    @Test
    fun testCustomHttpMethodWithBody() {

        val source = SourceFile.kotlin(
            "Source.kt", """     
package com.example.api
import de.jensklingenberg.ktorfit.http.HTTP
import de.jensklingenberg.ktorfit.http.Body

interface TestService {

    @HTTP("GET2","user",true)
    suspend fun test(@Body body: String): String
    
}"""
        )


        val expectedFunctionText = """val _ext: HttpRequestBuilder.() -> Unit = {
        method = HttpMethod.parse("GET2")
        url{
        takeFrom(ktorfitClient.baseUrl + "user")
        }
        setBody(body) 
        }"""

        val compilation = getCompilation(listOf(source))
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
    fun whenMultipleHttpMethodsFound_throwError() {
        val source = SourceFile.kotlin(
            "Source.kt", """
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
    """
        )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertThat(result.exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.ONLY_ONE_HTTP_METHOD_IS_ALLOWED))
    }
}