package de.jensklingenberg.ktorfit.model.annotations

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import de.jensklingenberg.ktorfit.model.KtorfitError
import de.jensklingenberg.ktorfit.utils.*

/**
 * Annotation at a parameter
 */
sealed class ParameterAnnotation
object Body : ParameterAnnotation()
object RequestBuilder : ParameterAnnotation()
data class Path(val value: String, val encoded: Boolean = false) : ParameterAnnotation()
data class Query(val value: String, val encoded: Boolean = false) : ParameterAnnotation()
data class QueryName(val encoded: Boolean = false) : ParameterAnnotation()
data class QueryMap(val encoded: Boolean = false) : ParameterAnnotation()
data class Header(val path: String) : ParameterAnnotation()
object HeaderMap : ParameterAnnotation()
object Url : ParameterAnnotation()
data class RequestType(val requestType: KSType) : ParameterAnnotation()
data class Field(val value: String, val encoded: Boolean = false) : ParameterAnnotation()
data class FieldMap(val encoded: Boolean) : ParameterAnnotation()
data class Part(val value: String = "", val encoding: String = "binary") : ParameterAnnotation()
data class PartMap(val encoding: String = "binary") : ParameterAnnotation()


/**
 *
 */
fun KSValueParameter.getParamAnnotationList(logger: KSPLogger): List<ParameterAnnotation> {
    val ksValueParameter = this
    val KEY_MAP = "Map"
    val KEY_STRING = "String"

    val paramAnnos = mutableListOf<ParameterAnnotation>()

    ksValueParameter.getHeaderAnnotation()?.let {
        paramAnnos.add(it)
    }
    ksValueParameter.getRequestBuilderAnnotation()?.let {
        paramAnnos.add(it)
    }
    ksValueParameter.getQueryAnnotation()?.let {
        paramAnnos.add(it)
    }

    ksValueParameter.getQueryNameAnnotation()?.let {
        paramAnnos.add(it)
    }

    ksValueParameter.getFieldAnnotation()?.let {
        paramAnnos.add(it)
    }

    ksValueParameter.getBodyAnnotation()?.let {
        if (ksValueParameter.type.resolve().isMarkedNullable) {
            logger.error(KtorfitError.BODY_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE, ksValueParameter.type)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getPathAnnotation()?.let {
        if (ksValueParameter.type.resolve().isMarkedNullable) {
            logger.error(KtorfitError.PATH_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE, ksValueParameter.type)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getHeaderMapAnnotation()?.let {
        //TODO: Find out how isAssignableFrom works
        if (!ksValueParameter.type.toString().endsWith(KEY_MAP)) {
            logger.error(KtorfitError.HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP, ksValueParameter)
        }
        val mapKey = ksValueParameter.type.resolve().arguments.first()
        if (mapKey.type.toString() != KEY_STRING || mapKey.type?.resolve()?.isMarkedNullable == true) {
            logger.error(KtorfitError.HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING, ksValueParameter)
        }
        paramAnnos.add(it)
    }



    ksValueParameter.getQueryMapAnnotation()?.let {
        if (!ksValueParameter.type.toString().endsWith(KEY_MAP)) {
            logger.error(KtorfitError.QUERY_MAP_PARAMETER_TYPE_MUST_BE_MAP, ksValueParameter)
        }

        val mapKey = ksValueParameter.type.resolve().arguments.first()
        if (mapKey.type.toString() != KEY_STRING || mapKey.type?.resolve()?.isMarkedNullable == true) {
            logger.error(KtorfitError.QUERY_MAP_KEYS_MUST_BE_OF_TYPE_STRING, ksValueParameter)
        }
        paramAnnos.add(it)
    }



    ksValueParameter.getFieldMapAnnotation()?.let {
        if (!ksValueParameter.type.toString().endsWith(KEY_MAP)) {
            logger.error(KtorfitError.FIELD_MAP_PARAMETER_TYPE_MUST_BE_MAP, ksValueParameter)
        }

        val mapKey = ksValueParameter.type.resolve().arguments.first()
        if (mapKey.type.toString() != KEY_STRING || mapKey.type?.resolve()?.isMarkedNullable == true) {
            logger.error(KtorfitError.FIELD_MAP_KEYS_MUST_BE_OF_TYPE_STRING, ksValueParameter)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getPartAnnotation()?.let {
        if (ksValueParameter.type.resolve().isMarkedNullable) {
            logger.error(KtorfitError.PART_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE, ksValueParameter.type)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getPartMapAnnotation()?.let {
        if (!ksValueParameter.type.toString().endsWith(KEY_MAP)) {
            logger.error(KtorfitError.PART_MAP_PARAMETER_TYPE_MUST_BE_MAP, ksValueParameter)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getUrlAnnotation()?.let {
        if (ksValueParameter.type.resolve().isMarkedNullable) {
            logger.error(KtorfitError.URL_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE, ksValueParameter)
        }
        paramAnnos.add(it)
    }

    ksValueParameter.getRequestTypeAnnotation()?.let {
        paramAnnos.add(it)
    }
    return paramAnnos
}