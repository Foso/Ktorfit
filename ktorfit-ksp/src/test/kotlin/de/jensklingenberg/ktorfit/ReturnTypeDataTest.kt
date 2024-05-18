package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        url{
        takeFrom(_ktorfit.baseUrl + "user")
        }
        setBody(id) 
        }"""

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)


        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        assertTrue(generatedFile.exists())

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedBodyDataArgumentText))
    }
}