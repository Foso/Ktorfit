package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.JvmTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCompilerApi::class)
class FunctionTransformerTest {
    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun whenCreateFuncWithInterfaceFound_TransformItToUseTheImplementation() {
        val source =
            SourceFile.kotlin(
                "Ktorfit.kt",
                """
package de.jensklingenberg.ktorfit

class Ktorfit(){
    fun <T> create(ktorfitService: ClassProvider<T>? = null): T {
        return ktorfitService as T
    }
}

interface ClassProvider<T>

     """,
            )
        val source2 =
            SourceFile.kotlin(
                "Main.kt",
                """
                package com.example.api
                import de.jensklingenberg.ktorfit.Ktorfit
                
                import de.jensklingenberg.ktorfit.ClassProvider
                
                interface TestService
                class _TestServiceImpl : TestService{
                
                }
                class _TestServiceProvider : ClassProvider<TestService>
                class TestClass{
                val api = Ktorfit().create<TestService>()
                }

                """.trimIndent(),
            )

        val result = compile(listOf(source2, source))

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        assertTrue(result.messages.contains("_TestServiceProvider"))
    }

    @Test
    fun throwCompileErrorWhenNonInterfaceTypeArgumentIsUsed() {
        val source =
            SourceFile.kotlin(
                "Ktorfit.kt",
                """
package de.jensklingenberg.ktorfit

class Ktorfit()

interface ClassProvider

fun <T> Ktorfit.create(ktorfitService: ClassProvider? = null): T {
    return this.create(ktorfitService)
}

     """,
            )
        val source2 =
            SourceFile.kotlin(
                "Main.kt",
                """
                package com.example.api
                import de.jensklingenberg.ktorfit.Ktorfit
                import de.jensklingenberg.ktorfit.create
                import de.jensklingenberg.ktorfit.ClassProvider
                
                interface TestService
                class _TestServiceImpl : TestService{
                companion object : ClassProvider
                }

                class TestClass{

                fun <T> test() : T{
                    return Ktorfit().create<T>()
                }
                }

                """.trimIndent(),
            )

        val result = compile(listOf(source2, source))
        assertEquals(KotlinCompilation.ExitCode.INTERNAL_ERROR, result.exitCode)
        assertTrue(result.messages.contains(CreateFuncTransformer.errorTypeArgumentNotInterface("T")))
    }

    private fun prepareCompilation(sourceFiles: List<SourceFile>): KotlinCompilation =
        KotlinCompilation().apply {
            languageVersion = "1.9"
            workingDir = temporaryFolder.root
            compilerPluginRegistrars = listOf(CommonCompilerPluginRegistrar())
            val processor = ExampleCommandLineProcessor()
            pluginOptions =
                listOf(
                    PluginOption("ktorfitPlugin", "enabled", true.toString()),
                    PluginOption("ktorfitPlugin", "logging", true.toString()),
                )
            commandLineProcessors = listOf(processor)
            inheritClassPath = true
            sources = sourceFiles
            verbose = false
            jvmTarget = JvmTarget.fromString(System.getProperty("rdt.jvmTarget", "11"))!!.description
            supportsK2 = false
        }

    private fun compile(sourceFiles: List<SourceFile>): JvmCompilationResult = prepareCompilation(sourceFiles).compile()
}
