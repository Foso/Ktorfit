package de.jensklingenberg.ktorfit

import KtorfitProcessorProvider
import com.google.common.truth.Truth
import com.tschuchort.compiletesting.*
import de.jensklingenberg.ktorfit.model.KtorfitError
import org.junit.Assert
import org.junit.Test
import java.io.File

class UrlTest {


    @Test
    fun testFunctionWithGET() {
        val expectedFunctionSource = """public override suspend fun test(): String {
    val _ext: HttpRequestBuilder.() -> Unit = {
        this.method = HttpMethod.parse("GET") 
        }
    val requestData = RequestData(relativeUrl="user",
        returnTypeData = TypeData("kotlin.String"),
        requestTypeInfo=typeInfo<String>(),
        returnTypeInfo = typeInfo<String>(),
        ktorfitRequestBuilder = _ext) 

    return ktorfitClient.suspendRequest<String, String>(requestData)!!
  }"""

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
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expectedFunctionSource)).isTrue()
    }

    @Test
    fun testFunctionWithGETAndUrlAnno() {
        val expectedFunctionSource = """public override suspend fun test(url: String): String {
    val _ext: HttpRequestBuilder.() -> Unit = {
        this.method = HttpMethod.parse("GET") 
        }
    val requestData = RequestData(relativeUrl="$\{url}",
        returnTypeData = TypeData("kotlin.String"),
        requestTypeInfo=typeInfo<String>(),
        returnTypeInfo = typeInfo<String>(),
        ktorfitRequestBuilder = _ext) 

    return ktorfitClient.suspendRequest<String, String>(requestData)!!
  }""".replace("\\{", "{")


        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Url

interface TestService {
    @GET("")
    suspend fun test(@Url url: String): String
}
    """
        )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expectedFunctionSource)).isTrue()
    }


    //Multiple
    @Test
    fun whenHttpMethodAnnotationPathEmptyAndNoUrlAnno_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET

interface TestService {
    @GET("")
    suspend fun test(): String
}
    """
        )


        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.MISSING_EITHER_KEYWORD_URL_OrURL_PARAMETER("GET")))
    }

    @Test
    fun whenMultipleParameterWithUrlAnnotation_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Url

interface TestService {
    @GET("")
    suspend fun test(@Url url: String, @Url url2: String): String
}
    """
        )


        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
        Assert.assertTrue(result.messages.contains(KtorfitError.MULTIPLE_URL_METHOD_ANNOTATIONS_FOUND))
    }



}

