package de.jensklingenberg.ktorfit.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FileSpec
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.WILDCARDIMPORT
import de.jensklingenberg.ktorfit.model.ktorfitClass
import java.io.File

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


fun String.surroundWith(s: String): String {
    return s + this + s
}

/**
 * Gets the imports of a class by reading the imports from the file
 * which contains the class
 *  TODO: Find better way to get imports
 */
fun KSClassDeclaration.getFileImports(): List<String> {
    val importList =
        File(this.containingFile!!.filePath)
            .readLines()
            .filter { it.trimStart().startsWith("import") }
            .filter { !it.startsWith("import de.jensklingenberg.ktorfit.http.") }
            .toMutableSet()

    importList.add(ktorfitClass.packageName + "." + ktorfitClass.name)
    importList.add("de.jensklingenberg.ktorfit.internal.*")

    return importList.map { it.removePrefix("import ") }
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
        val className = it.substringAfterLast(".").replace("*", WILDCARDIMPORT)

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