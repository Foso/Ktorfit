package de.jensklingenberg.ktorfit.utils

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Body
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Field
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.FieldMap
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Header
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.HeaderMap
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Part
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.PartMap
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Path
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Query
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.QueryMap
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.QueryName
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.RequestBuilder
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.RequestType
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Tag
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Url

/**
 * Default value used for annotation parameters because they can't be nullable
 */
private const val KTORFIT_DEFAULT_VALUE = "KTORFIT_DEFAULT_VALUE"

@OptIn(KspExperimental::class)
fun KSValueParameter.getPathAnnotation(): Path? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Path::class).firstOrNull()?.let {
        return Path(it.value.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()), it.encoded)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getHeaderAnnotation(): Header? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Header::class).firstOrNull()?.let {
        return Header(it.value)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getHeaderMapAnnotation(): HeaderMap? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.HeaderMap::class).firstOrNull()?.let {
        return HeaderMap
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getQueryAnnotation(): Query? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Query::class).firstOrNull()?.let {
        return Query(it.value.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()), it.encoded)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getQueryNameAnnotation(): QueryName? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.QueryName::class).firstOrNull()?.let {
        return QueryName(it.encoded)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getQueryMapAnnotation(): QueryMap? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.QueryMap::class).firstOrNull()?.let {
        return QueryMap(it.encoded)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getFieldAnnotation(): Field? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Field::class).firstOrNull()?.let {
        return Field(it.value.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()), it.encoded)
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
fun KSValueParameter.getReturnType(): ParameterAnnotation.ReturnType? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.ReturnType::class).firstOrNull()?.let {
        return ParameterAnnotation.ReturnType
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getPartMapAnnotation(): PartMap? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.PartMap::class).firstOrNull()?.let {
        return PartMap(it.encoding)
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getRequestBuilderAnnotation(): RequestBuilder? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.ReqBuilder::class).firstOrNull()?.let {
        return RequestBuilder
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getUrlAnnotation(): Url? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Url::class).firstOrNull()?.let {
        return Url
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getBodyAnnotation(): Body? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Body::class).firstOrNull()?.let {
        return Body
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getTagAnnotation(): Tag? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Tag::class).firstOrNull()?.let {
        return Tag(it.value.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()))
    }
}

fun KSValueParameter.getRequestTypeAnnotation(): RequestType? {
    val requestTypeClazz = de.jensklingenberg.ktorfit.http.RequestType::class
    val filteredAnnotations =
        this.annotations.filter {
            it.shortName.getShortName() == requestTypeClazz.simpleName &&
                it.annotationType
                    .resolve()
                    .declaration.qualifiedName
                    ?.asString() == requestTypeClazz.qualifiedName
        }
    return filteredAnnotations
        .mapNotNull {
            it.arguments
                .map { arg ->
                    RequestType((arg.value as KSType))
                }.firstOrNull()
        }.firstOrNull()
}
