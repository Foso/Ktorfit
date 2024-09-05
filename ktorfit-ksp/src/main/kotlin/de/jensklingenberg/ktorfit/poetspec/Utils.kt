package de.jensklingenberg.ktorfit.poetspec

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.toTypeName
import java.io.File

fun KSType.findTypeName(filePath: String): TypeName =
    if (this.isError) {
        val className =
            this
                .toString()
                .substringAfter("<ERROR TYPE: ")
                .substringBefore(">")
        findTypeImport(className, filePath)
            ?: throw IllegalStateException("Import for $this not found")
    } else {
        try {
            this.toTypeName()
        } catch (e: Exception) {
            if (this.arguments.isNotEmpty()) {
                TypeVariableName(this.toString())
            } else {
                findTypeImport(this.toString(), filePath)
                    ?: TypeVariableName(this.toString())
            }
        }
    }

/**
 * This is needed because since Kotlin 2.0 KSP can't resolve a type that is not in the same module
 * So the only way to get the type is to read the file and find the import in case of an error type
 * This approach is not perfect because it won't work when wildcard imports are used.
 * Also reading the source file is slow and should be avoided if possible
 */
private fun findTypeImport(
    className: String,
    filePath: String,
): ClassName? {
    val file = File(filePath)
    val lines = file.readLines()
    val imports = lines.filter { it.startsWith("import") }.map { it.substringAfter("import ") }
    val classImport = imports.firstOrNull { it.endsWith(className) }
    return classImport?.let { ClassName(classImport.substringBeforeLast("."), className) }
}
