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
        kspWithCompilation = true
        this.sources = sources
        inheritClassPath = true
        kspIncremental = true

        configureKsp(true) {
            kspProcessorOptions = kspArgs
            symbolProcessorProviders +=
                buildList {
                    addAll(mutableListOf(KtorfitProcessorProvider()))
                }
        }
    }
