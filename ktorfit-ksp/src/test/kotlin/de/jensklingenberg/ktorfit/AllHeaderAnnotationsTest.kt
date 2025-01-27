package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AllHeaderAnnotationsTest {
    @Test
    fun whenHeadersHeaderMapAndHeaderAnnotationFound_AddHeader() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.HeaderMap


interface TestService {
@Headers(value = ["x:y","a:b"])
@GET("posts")
suspend fun test(@Header("testHeader") testParameterNonNullable: String?, @Header("testHeader") testParameterNullable: String?, @HeaderMap("testHeaderMap") testParameter2: Map<String,String>): String
}
    """,
            )

        val expectedHeadersArgumentText =
            "headers{\n" +
                    "        testParameterNonNullable?.let{ append(\"testHeader\", testParameterNonNullable) }\n" +
                    "        testParameterNullable?.let{ append(\"testHeader\", testParameterNullable) }\n" +
                    "        testParameter2.forEach{ append(it.key , it.value)}\n" +
                    "        append(\"x\", \"y\")\n" +
                    "        append(\"a\", \"b\")\n" +
                    "        }"

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/com/example/api/_TestServiceImpl.kt",
            )

        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(expectedHeadersArgumentText))
    }
}
