package de.jensklingenberg.ktorfit

import com.tschuchort.compiletesting.*

fun getCompilation(sources: List<SourceFile>, kspArgs : MutableMap<String,String> = mutableMapOf()): KotlinCompilation {
    return KotlinCompilation().apply {
        this.sources = sources
        inheritClassPath = true
        symbolProcessorProviders = listOf(KtorfitProcessorProvider())
        kspIncremental = true
        this.kspArgs = kspArgs
    }
}