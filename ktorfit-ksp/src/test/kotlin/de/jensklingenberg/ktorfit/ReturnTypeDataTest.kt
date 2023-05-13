package de.jensklingenberg.ktorfit

import com.google.common.truth.Truth
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Test
import java.io.File


class ReturnTypeDataTest {

    @Test
    fun testFunctionWithBody() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Body

interface TestService {
   @POST("user")
    suspend fun test(@Body id: String): Map<String,Int>
}
    """
        )


        val expectedBodyDataArgumentText =
            """val _ext: HttpRequestBuilder.() -> Unit = {
        method = HttpMethod.parse("POST")
        url(ktorfitClient.baseUrl + "user")
        setBody(id) 
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
        Truth.assertThat(actualSource.contains(expectedBodyDataArgumentText)).isTrue()
    }
}