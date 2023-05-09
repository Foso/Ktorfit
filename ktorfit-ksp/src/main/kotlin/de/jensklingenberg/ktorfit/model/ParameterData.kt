package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSValueParameter
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation
import de.jensklingenberg.ktorfit.model.annotations.RequestBuilder
import de.jensklingenberg.ktorfit.model.annotations.getParamAnnotationList
import de.jensklingenberg.ktorfit.utils.anyInstance
import de.jensklingenberg.ktorfit.utils.resolveTypeName

data class ParameterData(
    val name: String,
    val type: ReturnTypeData,
    val annotations: List<ParameterAnnotation> = emptyList(),

    ) {
    inline fun <reified T> findAnnotationOrNull(): T? {
        return this.annotations.firstOrNull { it is T } as? T
    }

    inline fun <reified T> hasAnnotation(): Boolean {
        return this.findAnnotationOrNull<T>() != null
    }

}

fun KSValueParameter.createParameterData(logger: KSPLogger): ParameterData {
    val ksValueParameter = this
    if (ksValueParameter.isVararg) {
        logger.error(KtorfitError.VARARG_NOT_SUPPORTED_USE_LIST_OR_ARRAY, ksValueParameter)
    }

    val parameterAnnotations = ksValueParameter.getParamAnnotationList(logger)

    val parameterName = ksValueParameter.name?.asString() ?: ""
    val parameterType = ksValueParameter.type.resolve()
    val hasRequestBuilderAnno = parameterAnnotations.anyInstance<RequestBuilder>()

    if (parameterAnnotations.isEmpty() && !hasRequestBuilderAnno) {
        logger.error(
            KtorfitError.NO_KTORFIT_ANNOTATION_FOUND_AT(parameterName),
            ksValueParameter
        )
    }

    if (hasRequestBuilderAnno && parameterType.resolveTypeName() != "[@kotlin.ExtensionFunctionType] Function1<HttpRequestBuilder, Unit>") {
        logger.error(
            KtorfitError.REQ_BUILDER_PARAMETER_TYPE_NEEDS_TO_BE_HTTP_REQUEST_BUILDER,
            ksValueParameter
        )
    }

    val type = if (hasRequestBuilderAnno) {
        ReturnTypeData(
            "HttpRequestBuilder.()->Unit",
            "HttpRequestBuilder.()->Unit",
            ksValueParameter.type
        )
    } else {
        ReturnTypeData(
            parameterType.resolveTypeName(),
            parameterType.declaration.qualifiedName?.asString() ?: "",
            ksValueParameter.type
        )
    }

    return ParameterData(
        parameterName,
        type,
        parameterAnnotations
    )

}
