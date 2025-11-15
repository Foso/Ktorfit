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
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Path::class).firstOrNull()?.let {
            return Path(it.value.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()), it.encoded)
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "Path"
        }?.let {
            val encoded = it.getArgumentValueByName<Boolean>("encoded") ?: false
            return Path(it.getArgumentValueByName<String>("value")?.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()) ?: this.name.safeString(), encoded)
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getHeaderAnnotation(): Header? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Header::class).firstOrNull()?.let {
            return Header(it.value)
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "Header"
        }?.let {
            return Header(it.getArgumentValueByName<String>("value") ?: this.name.safeString())
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getHeaderMapAnnotation(): HeaderMap? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.HeaderMap::class).firstOrNull()?.let {
            return HeaderMap
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "HeaderMap"
        }?.let {
            return HeaderMap
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getQueryAnnotation(): Query? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Query::class).firstOrNull()?.let {
            return Query(it.value.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()), it.encoded)
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "Query"
        }?.let {
            val encoded = it.getArgumentValueByName<Boolean>("encoded") ?: false
            return Query(it.getArgumentValueByName<String>("value")?.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()) ?: this.name.safeString(), encoded)
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getQueryNameAnnotation(): QueryName? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.QueryName::class).firstOrNull()?.let {
            return QueryName(it.encoded)
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "QueryName"
        }?.let {
            val encoded = it.getArgumentValueByName<Boolean>("encoded") ?: false
            return QueryName(encoded)
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getQueryMapAnnotation(): QueryMap? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.QueryMap::class).firstOrNull()?.let {
            return QueryMap(it.encoded)
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "QueryMap"
        }?.let {
            val encoded = it.getArgumentValueByName<Boolean>("encoded") ?: false
            return QueryMap(encoded)
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getFieldAnnotation(): Field? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Field::class).firstOrNull()?.let {
            return Field(it.value.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()), it.encoded)
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "Field"
        }?.let {
            val encoded = it.getArgumentValueByName<Boolean>("encoded") ?: false
            return Field(it.getArgumentValueByName<String>("value")?.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()) ?: this.name.safeString(), encoded)
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getFieldMapAnnotation(): FieldMap? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.FieldMap::class).firstOrNull()?.let {
            return FieldMap(it.encoded)
        }
    } catch (e: Exception) {
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "FieldMap"
        }?.let {
            val encoded = it.getArgumentValueByName<Boolean>("encoded") ?: false
            return FieldMap(encoded)
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getPartAnnotation(): Part? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Part::class).firstOrNull()?.let {
            return Part(it.value, "binary")
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "Part"
        }?.let {
            return Part(it.getArgumentValueByName<String>("value") ?: this.name.safeString(), "binary")
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getPartMapAnnotation(): PartMap? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.PartMap::class).firstOrNull()?.let {
            return PartMap(it.encoding)
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "PartMap"
        }?.let {
            return PartMap(it.getArgumentValueByName<String>("encoding") ?: "binary")
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getRequestBuilderAnnotation(): RequestBuilder? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.ReqBuilder::class).firstOrNull()?.let {
            return RequestBuilder
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "ReqBuilder"
        }?.let {
            return RequestBuilder
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getUrlAnnotation(): Url? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Url::class).firstOrNull()?.let {
            return Url
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "Url"
        }?.let {
            return Url
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getBodyAnnotation(): Body? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Body::class).firstOrNull()?.let {
            return Body
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "Body"
        }?.let {
            return Body
        }
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getReturnType(): ParameterAnnotation.ReturnType? {
    return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.ReturnType::class).firstOrNull()?.let {
        return ParameterAnnotation.ReturnType
    }
}

@OptIn(KspExperimental::class)
fun KSValueParameter.getTagAnnotation(): Tag? {
    try {
        return this.getAnnotationsByType(de.jensklingenberg.ktorfit.http.Tag::class).firstOrNull()?.let {
            return Tag(it.value.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()))
        }
    } catch (e: Exception) {
        // TODO Workaround for KSP2 cant find annotation,check if still needed when KSP2 out of beta
        return this.annotations.firstOrNull {
            it.shortName.getShortName() == "Tag"
        }?.let {
            return Tag(it.getArgumentValueByName<String>("value")?.replace(KTORFIT_DEFAULT_VALUE, this.name.safeString()) ?: this.name.safeString())
        }
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


