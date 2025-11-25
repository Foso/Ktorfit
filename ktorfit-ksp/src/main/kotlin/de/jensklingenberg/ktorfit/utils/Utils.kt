package de.jensklingenberg.ktorfit.utils

import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.squareup.kotlinpoet.FileSpec

/**
 * Recursively resolves typealias to its actual underlying type.
 * For example: typealias StringMap = Map<String, String> -> Map<String, String>
 */
fun KSType.resolveTypeAlias(): KSType {
    var currentType = this
    while (currentType.declaration is KSTypeAlias) {
        currentType = (currentType.declaration as KSTypeAlias).type.resolve()
    }
    return currentType
}

fun KSType?.resolveTypeName(): String {
    if (this == null) return ""

    // Resolve typealias to actual type and return its string representation
    return this.resolveTypeAlias().toString()
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
