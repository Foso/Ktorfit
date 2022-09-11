package de.jensklingenberg.ktorfit.parser

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSValueParameter
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.FIELD_MAP_KEYS_MUST_BE_OF_TYPE_STRING
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.FIELD_MAP_PARAMETER_TYPE_MUST_BE_MAP
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.NO_KTORFIT_ANNOTATION_FOUND_AT
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.PART_MAP_PARAMETER_TYPE_MUST_BE_MAP
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.PART_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.PATH_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.QUERY_MAP_KEYS_MUST_BE_OF_TYPE_STRING
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.QUERY_MAP_PARAMETER_TYPE_MUST_BE_MAP
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.REQ_BUILDER_PARAMETER_TYPE_NEEDS_TO_BE_HTTP_REQUEST_BUILDER
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.URL_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.VARARG_NOT_SUPPORTED_USE_LIST_OR_ARRAY
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.TypeData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation
import de.jensklingenberg.ktorfit.model.ktorfitError
import de.jensklingenberg.ktorfit.utils.*


fun getParameterData(ksValueParameter: KSValueParameter, logger: KSPLogger): ParameterData {
    if (ksValueParameter.isVararg) {
        logger.ktorfitError(VARARG_NOT_SUPPORTED_USE_LIST_OR_ARRAY, ksValueParameter)
    }
    if (ksValueParameter.type.resolve().isMarkedNullable) {
        //logger.ktorfitError(NULLABLE_PARAMETERS_ARE_NOT_SUPPORTED, ksValueParameter)
    }

    val parameterAnnotations = getParamAnnotationList(ksValueParameter, logger)

    val reqBuilderAnno = ksValueParameter.getRequestBuilderAnnotation()
    val parameterName = ksValueParameter.name?.asString() ?: ""
    val parameterType = ksValueParameter.type.resolve()
    val hasRequestBuilderAnno = reqBuilderAnno != null

    if (parameterAnnotations.isEmpty() && !hasRequestBuilderAnno) {
        logger.ktorfitError(
            NO_KTORFIT_ANNOTATION_FOUND_AT(parameterName),
            ksValueParameter
        )
    }



    if (hasRequestBuilderAnno && parameterType.resolveTypeName() != "[@kotlin.ExtensionFunctionType] Function1<HttpRequestBuilder, Unit>") {
        logger.ktorfitError(
            REQ_BUILDER_PARAMETER_TYPE_NEEDS_TO_BE_HTTP_REQUEST_BUILDER,
            ksValueParameter
        )
    }

    val type = if (hasRequestBuilderAnno) {
        TypeData(
            "HttpRequestBuilder.()->Unit",
            "HttpRequestBuilder.()->Unit"
        )
    } else {
        TypeData(
            parameterType.resolveTypeName(),
            parameterType.declaration.qualifiedName?.asString() ?: ""
        )
    }

    return ParameterData(parameterName, type, parameterAnnotations, hasRequestBuilderAnno)

}


fun getParamAnnotationList(ksValueParameter: KSValueParameter, logger: KSPLogger): List<ParameterAnnotation> {

    val paramAnnos = mutableListOf<ParameterAnnotation>()
    ksValueParameter.getBodyAnnotation()?.let {
        paramAnnos.add(it)
    }

    ksValueParameter.getPathAnnotation()?.let {
        if (ksValueParameter.type.resolve().isMarkedNullable) {
            logger.ktorfitError(PATH_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE, ksValueParameter.type)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getHeadersAnnotation()?.let {
        paramAnnos.add(it)
    }

    ksValueParameter.getHeaderMapAnnotation()?.let {
        //TODO: Find out how isAssignableFrom works
        if (!ksValueParameter.type.toString().endsWith("Map")) {
            logger.ktorfitError(HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP, ksValueParameter)
        }
        val mapKey = ksValueParameter.type.resolve().arguments.first()
        if (mapKey.type.toString() != "String" || mapKey.type?.resolve()?.isMarkedNullable == true) {
            logger.error(HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING, ksValueParameter)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getQueryAnnotation()?.let {
        paramAnnos.add(it)
    }

    ksValueParameter.getQueryNameAnnotation()?.let {
        paramAnnos.add(it)
    }

    ksValueParameter.getQueryMapAnnotation()?.let {
        if (!ksValueParameter.type.toString().endsWith("Map")) {
            logger.error(QUERY_MAP_PARAMETER_TYPE_MUST_BE_MAP, ksValueParameter)
        }

        val mapKey = ksValueParameter.type.resolve().arguments.first()
        if (mapKey.type.toString() != "String" || mapKey.type?.resolve()?.isMarkedNullable == true) {
            logger.error(QUERY_MAP_KEYS_MUST_BE_OF_TYPE_STRING, ksValueParameter)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getFieldAnnotation()?.let {
        paramAnnos.add(it)
    }

    ksValueParameter.getFieldMapAnnotation()?.let {
        if (!ksValueParameter.type.toString().endsWith("Map")) {
            logger.ktorfitError(FIELD_MAP_PARAMETER_TYPE_MUST_BE_MAP, ksValueParameter)
        }

        val mapKey = ksValueParameter.type.resolve().arguments.first()
        if (mapKey.type.toString() != "String" || mapKey.type?.resolve()?.isMarkedNullable == true) {
            logger.error(FIELD_MAP_KEYS_MUST_BE_OF_TYPE_STRING, ksValueParameter)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getPartAnnotation()?.let {
        if (ksValueParameter.type.resolve().isMarkedNullable) {
            logger.ktorfitError(PART_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE, ksValueParameter.type)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getPartMapAnnotation()?.let {
        if (!ksValueParameter.type.toString().endsWith("Map")) {
            logger.ktorfitError(PART_MAP_PARAMETER_TYPE_MUST_BE_MAP, ksValueParameter)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getUrlAnnotation()?.let {
        if (ksValueParameter.type.resolve().isMarkedNullable) {
            logger.ktorfitError(URL_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE, ksValueParameter)
        }
        paramAnnos.add(it)
    }
    return paramAnnos
}




