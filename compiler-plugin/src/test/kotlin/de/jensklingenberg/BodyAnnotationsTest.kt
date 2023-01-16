package de.jensklingenberg

import com.google.common.truth.Truth
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Test

class BodyAnnotationsTest {


    @Test
    fun whenBodyUsedWithNonBodyMethod_ThrowCompilationError() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.ktorfit

interface TestService {

    @GET("user")
    suspend fun test(@Body id: String): String?
    
}



    """
        )


        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
        }

        val result = compilation.compile()
        Truth.assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)

    }



}

