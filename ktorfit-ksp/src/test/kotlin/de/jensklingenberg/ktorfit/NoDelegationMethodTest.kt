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

class NoDelegationMethodTest {
    @Test
    fun `When function is annotated with NoDelegation is skipped of generated code`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
                package test

                import de.jensklingenberg.ktorfit.http.GET
                import de.jensklingenberg.ktorfit.core.NoDelegation
                import kotlinx.coroutines.runBlocking

                interface TestService {
                    @GET("/hello")
                    suspend fun test(): String

                    @GET("/hello/world")
                    @NoDelegation
                    fun test2(): String = runBlocking { test() }
                }
                """
            )

        val noExpectedOverrideMethod = """override fun test2()"""

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()

        assertFalse(result.messages.contains(KtorfitError.noDefaultImplWithNoDelegation("test2")))
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(
                generatedSourcesDir,
                "/kotlin/test/_TestServiceImpl.kt",
            )
        val actualSource = generatedFile.readText()
        assertFalse(actualSource.contains(noExpectedOverrideMethod))
    }

    @Test
    fun `When function is annotated with NoDelegation and don't have body, throw error`() {
        val source =
            SourceFile.kotlin(
                "Source.kt",
                """
                package test

                import de.jensklingenberg.ktorfit.http.GET
                import de.jensklingenberg.ktorfit.core.NoDelegation

                interface TestService {
                    @GET("/hello")
                    suspend fun test(): String

                    @GET("/hello/world")
                    @NoDelegation
                    fun test2(): String
                }
                """
            )

        val compilation = getCompilation(listOf(source))
        val result = compilation.compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains(KtorfitError.noDefaultImplWithNoDelegation("test2")))
    }
}
