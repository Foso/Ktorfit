package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class NestedInterfaceTest {
    @Test
    fun `when interface is declared inside an object Then generated code uses the qualified name`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET

data object Page {
    interface PageApi {
        @GET("page")
        suspend fun page(): String
    }
}
    """,
            )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedFile = File(compilation.kspSourcesDir, "/kotlin/com/example/api/_PageApiImpl.kt")
        val actualSource = generatedFile.readText()
        assertTrue(actualSource.contains(") : Page.PageApi {"))
        assertTrue(actualSource.contains("public fun Ktorfit.createPageApi(): Page.PageApi"))
        assertFalse(result.messages.contains("Unresolved reference 'PageApi'"))
    }

    @Test
    fun `when interface extends an interface declared inside an object Then generated code uses the qualified name`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """package com.example.api
import de.jensklingenberg.ktorfit.http.GET

object Container {
    interface SuperApi {
        @GET("super")
        suspend fun superCall(): String
    }
}

interface TestService : Container.SuperApi {
    @GET("test")
    suspend fun test(): String
}
    """,
            )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        val generatedFile = File(compilation.kspSourcesDir, "/kotlin/com/example/api/_TestServiceImpl.kt")
        val actualSource = generatedFile.readText()
        assertTrue(
            actualSource.contains(
                "Container.SuperApi by com.example.api._SuperApiImpl( _baseUrl,_httpClient,_helper)",
            ),
        )
        assertFalse(result.messages.contains("Unresolved reference 'SuperApi'"))
    }
}
