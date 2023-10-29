package de.jensklingenberg.ktorfit.utils

import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FileSpec
import de.jensklingenberg.ktorfit.model.FunctionData

fun KSType?.resolveTypeName(): String {
    //TODO: Find better way to handle type alias Types
    return this.toString().removePrefix("[typealias ").removeSuffix("]")
}

inline fun <reified T> FunctionData.findAnnotationOrNull(): T? {
    return this.annotations.firstOrNull { it is T } as? T
}

fun String.prefixIfNotEmpty(s: String): String {
    return (s + this).takeIf { this.isNotEmpty() } ?: this
}

fun String.postfixIfNotEmpty(s: String): String {
    return (this + s).takeIf { this.isNotEmpty() } ?: this
}

fun String.surroundIfNotEmpty(prefix: String = "", postFix: String = ""): String {
    return this.prefixIfNotEmpty(prefix).postfixIfNotEmpty(postFix)
}

fun String.removeWhiteSpaces(): String {
    return this.replace("\\s".toRegex(), "")
}


fun FileSpec.Builder.addImports(imports: List<String>): FileSpec.Builder {

    imports.forEach {
        /**
         * Wildcard imports are not allowed by KotlinPoet, as a workaround * is replaced with WILDCARDIMPORT, and it will be replaced again
         * after Kotlin Poet generated the source code
         */
        val packageName = it.substringBeforeLast(".")
        val className = it.substringAfterLast(".")

        this.addImport(packageName, className)
    }
    return this
}

inline fun <reified T> List<*>.anyInstance(): Boolean {
    return this.filterIsInstance<T>().isNotEmpty()
}

fun KSName?.safeString(): String {
    return this?.asString() ?: ""
}