package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.symbolProcessorProviders

fun getCompilation(sources: List<SourceFile>): KotlinCompilation {
    return KotlinCompilation().apply {
        this.sources = sources
        inheritClassPath = true
        symbolProcessorProviders = listOf(KtorfitProcessorProvider())
        kspIncremental = true
    }
}