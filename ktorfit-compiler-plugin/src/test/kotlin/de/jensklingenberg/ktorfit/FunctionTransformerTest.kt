package de.jensklingenberg.ktorfit


import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.config.JvmTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FunctionTransformerTest {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun whenCreateFuncWithInterfaceFound_TransformItToUseTheImplementation() {
        val source = SourceFile.kotlin(
            "Ktorfit.kt", """
package de.jensklingenberg.ktorfit

class Ktorfit()

interface KtorfitService

class Default : KtorfitService

fun <T> Ktorfit.create(ktorfitService: KtorfitService = Default()): T {
    return this.create(ktorfitService)
}

     """
        )
        val source2 = SourceFile.kotlin(
            "Main.kt", """
                package com.example.api
                import de.jensklingenberg.ktorfit.Ktorfit
                import de.jensklingenberg.ktorfit.create
                import de.jensklingenberg.ktorfit.KtorfitService
                
                interface TestService
                class _TestServiceImpl : TestService, KtorfitService

                class TestClass{
                val api = Ktorfit().create<TestService>()
                }

            """.trimIndent()
        )

        val result = compile(listOf(source2, source))

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        assertTrue(result.messages.contains("_TestServiceImpl"))

    }

    @Test
    fun throwCompileErrorWhenNonInterfaceTypeArgumentIsUsed() {
        val source = SourceFile.kotlin(
            "Ktorfit.kt", """
package de.jensklingenberg.ktorfit

class Ktorfit()

interface KtorfitService

class Default : KtorfitService

fun <T> Ktorfit.create(ktorfitService: KtorfitService = Default()): T {
    return this.create(ktorfitService)
}

     """
        )
        val source2 = SourceFile.kotlin(
            "Main.kt", """
                package com.example.api
                import de.jensklingenberg.ktorfit.Ktorfit
                import de.jensklingenberg.ktorfit.create
                import de.jensklingenberg.ktorfit.KtorfitService
                
                interface TestService
                class _TestServiceImpl : TestService, KtorfitService

                class TestClass{

                fun <T> test() : T{
                    return Ktorfit().create<T>()
                }
                }

            """.trimIndent()
        )

        val result = compile(listOf(source2, source))
        assertEquals(KotlinCompilation.ExitCode.INTERNAL_ERROR,result.exitCode)
        assertTrue(result.messages.contains(CreateFuncTransformer.ERROR_TYPE_ARGUMENT_NOT_INTERFACE("T")))
    }

    private fun prepareCompilation(
        sourceFiles: List<SourceFile>
    ): KotlinCompilation {
        return KotlinCompilation().apply {
            workingDir = temporaryFolder.root
            compilerPluginRegistrars = listOf(CommonCompilerPluginRegistrar())
            val processor = ExampleCommandLineProcessor()
            pluginOptions = listOf(
                PluginOption("ktorfitPlugin", "enabled", true.toString()),
                PluginOption("ktorfitPlugin", "logging", true.toString())
            )
            commandLineProcessors = listOf(processor)
            inheritClassPath = true
            sources = sourceFiles
            verbose = false
            jvmTarget = JvmTarget.fromString(System.getProperty("rdt.jvmTarget", "1.8"))!!.description
            supportsK2 = false
        }
    }

    private fun compile(
        sourceFiles: List<SourceFile>
    ): JvmCompilationResult {
        return prepareCompilation(sourceFiles).compile()
    }
}

