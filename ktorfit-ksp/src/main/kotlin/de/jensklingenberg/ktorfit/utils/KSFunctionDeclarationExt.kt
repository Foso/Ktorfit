package de.jensklingenberg.ktorfit.utils

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import de.jensklingenberg.ktorfit.model.annotations.CustomHttp
import de.jensklingenberg.ktorfit.model.annotations.FormUrlEncoded
import de.jensklingenberg.ktorfit.model.annotations.Headers
import de.jensklingenberg.ktorfit.model.annotations.HttpMethod
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.Multipart
import de.jensklingenberg.ktorfit.model.annotations.Streaming

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.getHeaderAnnotation(): Headers? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Headers::class).firstOrNull()?.let { headers ->
        return Headers(headers.value.toList())
    }
}

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.getFormUrlEncodedAnnotation(): FormUrlEncoded? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.FormUrlEncoded::class).firstOrNull()?.let {
        return FormUrlEncoded
    }
}

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.getStreamingAnnotation(): Streaming? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Streaming::class).firstOrNull()?.let {
        return Streaming
    }
}

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.getMultipartAnnotation(): Multipart? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Multipart::class).firstOrNull()?.let {
        return Multipart
    }
}

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.parseHTTPMethodAnno(name: String): HttpMethodAnnotation? =
    when (val annotation = this.getAnnotationByName(name)) {
        null -> {
            null
        }

        else -> {
            if (name == "HTTP") {
                this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.HTTP::class).firstOrNull()?.let {
                    CustomHttp(it.path, HttpMethod.CUSTOM, it.method)
                }
            } else {
                val value = annotation.getArgumentValueByName<String>("value").orEmpty()
                HttpMethodAnnotation(value, HttpMethod.valueOf(name))
            }
        }
    }

private fun KSFunctionDeclaration.getAnnotationByName(name: String): KSAnnotation? =
    this.annotations.toList().firstOrNull {
        it.shortName.asString() == name
    }

fun <T> KSAnnotation.getArgumentValueByName(name: String): T? = this.arguments.firstOrNull { it.name?.asString() == name }?.value as? T

val KSFunctionDeclaration.isSuspend: Boolean
    get() = (this).modifiers.contains(Modifier.SUSPEND)

fun KSDeclaration.getKsFile(): KSFile = this.containingFile ?: throw Error("Containing File for ${this.simpleName} was null")
