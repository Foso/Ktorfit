package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class HeadersAnnotationsTest {

    @Test
    fun whenHeadersAnnotationFound_AddHeader() {

        val source = SourceFile.kotlin(
            "Source.kt", """
      package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers

interface TestService {

@Headers(value = ["x:y","a:b"])
@GET("posts")
suspend fun test(): String
}
    """
        )

        val expectedHeadersArgumentText = "headers{\n" +
                "        append(\"x\", \"y\")\n" +
                "        append(\"a\", \"b\")\n" +
                "        } "

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "/kotlin/com/example/api/_TestServiceImpl.kt"
        )
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }


}

