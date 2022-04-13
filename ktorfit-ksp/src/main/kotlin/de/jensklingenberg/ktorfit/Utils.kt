package de.jensklingenberg.ktorfit

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import de.jensklingenberg.ktorfit.model.MyFunction
import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.annotations.*

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
                    MyHttp(it.path, HttpMethod.valueOf(it.method), it.hasBody)
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

@OptIn(KspExperimental::class)
fun KSValueParameter.getPathAnnotation(): Path? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Path::class).firstOrNull()?.let {
        return Path(it.value, it.encoded)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getHeadersAnnotation(): Header? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Header::class).firstOrNull()?.let {
        return Header(it.value)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getQueryAnnotation(): Query? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Query::class).firstOrNull()?.let {
        return Query(it.value, it.encoded)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getQueryNameAnnotation(): QueryName? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.QueryName::class).firstOrNull()?.let {
        return QueryName(it.encoded)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getFieldAnnotation(): Field? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Field::class).firstOrNull()?.let {
        return Field(it.value, it.encoded)
    }
}


@OptIn(KspExperimental::class)
fun KSValueParameter.getFieldMapAnnotation(): FieldMap? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.FieldMap::class).firstOrNull()?.let {
        return FieldMap(it.encoded)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getPartAnnotation(): Part? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Part::class).firstOrNull()?.let {
        return Part(it.value, "binary")
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getPartMapAnnotation(): PartMap? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.PartMap::class).firstOrNull()?.let {
        return PartMap(it.encoding)
    }
}

val KSFunctionDeclaration.isSuspend: Boolean
    get() = (this).modifiers.contains(Modifier.SUSPEND)

@OptIn(KspExperimental::class)
fun KSValueParameter.getRequestBuilderAnnotation(): RequestBuilder? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.ReqBuilder::class).firstOrNull()?.let {
        return RequestBuilder()
    }
}


@OptIn(KspExperimental::class)
fun KSValueParameter.getQueryMapAnnotation(): QueryMap? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.QueryMap::class).firstOrNull()?.let {
        return QueryMap(it.encoded)
    }
}


@OptIn(KspExperimental::class)
fun KSValueParameter.getHeaderMapAnnotation(): HeaderMap? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.HeaderMap::class).firstOrNull()?.let {
        return HeaderMap()
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getUrlAnnotation(): Url? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Url::class).firstOrNull()?.let {
        return Url()
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getBodyAnnotation(): Body? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Body::class).firstOrNull()?.let {
        return Body()
    }
}

fun KSType?.resolveTypeName(): String {
    //TODO: Find better way to handle type alias Types
    return this.toString().removePrefix("[typealias ").removeSuffix("]")
}


inline fun <reified T> MyFunction.findAnnotationOrNull(): T? {
    return this.annotations.firstOrNull { it is T } as? T
}

inline fun <reified T> MyParam.findAnnotationOrNull(): T? {
    return this.annotations.firstOrNull { it is T } as? T
}

inline fun <reified T> MyParam.hasAnnotation(): Boolean {
    return this.findAnnotationOrNull<T>() != null
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


public fun KSPLogger.ktorfitError(s: String, classDec: KSNode) {
    this.error("Ktorfit: $s", classDec)
}


fun String.surroundWith(s: String): String {
    return s + this + s
}
