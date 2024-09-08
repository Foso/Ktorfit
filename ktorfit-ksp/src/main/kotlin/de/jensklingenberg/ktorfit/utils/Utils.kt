package de.jensklingenberg.ktorfit.utils

import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FileSpec

fun KSType?.resolveTypeName(): String {
    // TODO: Find better way to handle type alias Types
    return this.toString().removePrefix("[typealias ").removeSuffix("]")
}

fun String.prefixIfNotEmpty(s: String): String = (s + this).takeIf { this.isNotEmpty() } ?: this

fun String.postfixIfNotEmpty(s: String): String = (this + s).takeIf { this.isNotEmpty() } ?: this

fun String.surroundIfNotEmpty(
    prefix: String,
    postFix: String,
): String = this.prefixIfNotEmpty(prefix).postfixIfNotEmpty(postFix)

fun String.removeWhiteSpaces(): String = this.replace("\\s".toRegex(), "")

fun FileSpec.Builder.addImports(imports: Set<String>): FileSpec.Builder {
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

inline fun <reified T> List<*>.anyInstance(): Boolean = this.filterIsInstance<T>().isNotEmpty()

fun KSName?.safeString(): String = this?.asString() ?: ""
