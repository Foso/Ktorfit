package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
fun getCompilation(
    sources: List<SourceFile>,
    kspArgs: MutableMap<String, String> = mutableMapOf(),
): KotlinCompilation =
    KotlinCompilation().apply {
        this.sources = sources
        inheritClassPath = true
        symbolProcessorProviders = listOf(KtorfitProcessorProvider())
        kspIncremental = true
        this.kspArgs = kspArgs
    }
