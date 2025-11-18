package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspProcessorOptions
import com.tschuchort.compiletesting.kspWithCompilation
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
fun getCompilation(
    sources: List<SourceFile>,
    kspArgs: MutableMap<String, String> = mutableMapOf(),
): KotlinCompilation =
    KotlinCompilation().apply {
        this.sources = sources
        inheritClassPath = true

        configureKsp {
            kspProcessorOptions = kspArgs
            symbolProcessorProviders += KtorfitProcessorProvider()
        }
        kspIncremental = true
        kspWithCompilation = true
    }
