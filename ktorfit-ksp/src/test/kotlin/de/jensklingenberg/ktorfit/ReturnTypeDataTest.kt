package de.jensklingenberg.ktorfit

import KtorfitProcessorProvider
import com.google.common.truth.Truth
import com.tschuchort.compiletesting.*
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
            """returnTypeData = TypeData("kotlin.collections.Map",listOf(TypeData("kotlin.String"),
            TypeData("kotlin.Int")))"""

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        Truth.assertThat(generatedFile.exists()).isTrue()
        Truth.assertThat(generatedFile.readText().contains(expectedBodyDataArgumentText)).isTrue()
    }
}