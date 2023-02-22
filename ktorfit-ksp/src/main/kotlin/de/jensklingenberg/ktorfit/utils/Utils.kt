package de.jensklingenberg.ktorfit.utils

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.FileSpec
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.WILDCARDIMPORT
import de.jensklingenberg.ktorfit.model.annotations.*
import de.jensklingenberg.ktorfit.model.ktorfitClass
import java.io.File

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.getHeadersAnnotation(): Headers? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Headers::class).firstOrNull()?.let { headers ->
        return Headers(headers.value.toList())
    }
}

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.getFormUrlEncodedAnnotation(): FormUrlEncoded? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.FormUrlEncoded::class).firstOrNull()?.let {
        return FormUrlEncoded()
    }
}

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.getStreamingAnnotation(): Streaming? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Streaming::class).firstOrNull()?.let {
        return Streaming()
    }
}

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.getMultipartAnnotation(): Multipart? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Multipart::class).firstOrNull()?.let {
        return Multipart()
    }
}

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.parseHTTPMethodAnno(name: String): HttpMethodAnnotation? {
    return when (val annotation = this.getAnnotationByName(name)) {
        null -> {
            null
        }

        else -> {

            if (name == "HTTP") {
                this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.HTTP::class).firstOrNull()?.let {
                    CustomHttp(it.path, HttpMethod.valueOf(it.method), it.hasBody)
                }

            } else {
                val value = annotation.getArgumentValueByName<String>("value") ?: ""
                HttpMethodAnnotation(value, HttpMethod.valueOf(name))
            }

        }
    }
}

fun KSFunctionDeclaration.getAnnotationByName(name: String): KSAnnotation? {
    return this.annotations.toList().firstOrNull { it.shortName.asString() == name }
}

fun <T> KSAnnotation.getArgumentValueByName(name: String): T? {
    return this.arguments.firstOrNull { it.name?.asString() == name }?.value as? T
}


val KSFunctionDeclaration.isSuspend: Boolean
    get() = (this).modifiers.contains(Modifier.SUSPEND)


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