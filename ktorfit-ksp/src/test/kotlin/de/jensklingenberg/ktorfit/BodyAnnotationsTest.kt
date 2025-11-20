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

class BodyAnnotationsTest {
    @Test
    fun whenBodyUsedWithFormUrlEncoded_ThrowCompilationError() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.core.TypeConverter
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

interface TestService {
    @FormUrlEncoded
    @POST("user")
    suspend fun test(@Body id: String): String
}

class MyConv {

    @TypeConverter
    suspend fun conv(httpResponse: HttpResponse): Int {
        return httpResponse.body<Int>()
    }

}
    """,
            )

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.BODY_PARAMETERS_CANNOT_BE_USED_WITH_FORM_OR_MULTI_PART_ENCODING))
    }

    @Test
    fun testFunctionWithBody() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
      package com.example.api
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Body

interface TestService {
   @POST("user")
    suspend fun test(@Body id: String?): String
}
    """,
            )

        val expectedBodyDataArgumentText = "setBody(id)"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )
        assertTrue(generatedFile.exists())
        assertTrue(generatedFile.readText().contains(expectedBodyDataArgumentText))
    }

    @Test
    fun whenNoBodyAnnotationsFound_KeepBodysArgumentEmpty() {
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

        val notExpectedBodyDataArgumentText = "setBody("

        val compilation = getCompilation(listOf(source))

        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        assertTrue(generatedFile.exists())
        assertFalse(generatedFile.readText().contains(notExpectedBodyDataArgumentText))
    }
}
