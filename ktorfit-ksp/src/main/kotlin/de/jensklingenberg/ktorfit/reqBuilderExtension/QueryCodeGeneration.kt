package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Query
import de.jensklingenberg.ktorfit.model.annotations.QueryMap
import de.jensklingenberg.ktorfit.model.annotations.QueryName

fun getQueryCode(params: List<ParameterData>, listType: KSType, arrayType: KSType): String {

    val queryText = getQueryText(params, listType, arrayType)

    val queryNameText = getQueryNameText(params, listType, arrayType)

    val queryMapStrings = getQueryMapText(params)

    return (queryText + queryNameText + queryMapStrings)
}

private fun getQueryMapText(params: List<ParameterData>) =
    params.filter { it.hasAnnotation<QueryMap>() }.joinToString(separator = "") { parameterData ->
        val queryMap = parameterData.findAnnotationOrNull<QueryMap>()!!
        val encoded = queryMap.encoded
        val data = parameterData.name
        "%s?.forEach { entry -> entry.value?.let{ %s(entry.key, \"\${entry.value}\") } }\n".format(
            data,
            if (encoded) {
                "encodedParameters.append"
            } else {
                "parameter"
            }
        )

    }

private fun getQueryNameText(
    params: List<ParameterData>,
    listType: KSType,
    arrayType: KSType
) = params.filter { it.hasAnnotation<QueryName>() }.joinToString(separator = "") { parameterData ->
    val queryName = parameterData.annotations.filterIsInstance<QueryName>().first()
    val encoded = queryName.encoded
    val name = parameterData.name

    val starProj = parameterData.type.parameterType?.resolve()?.starProjection()
    val isList = starProj?.isAssignableFrom(listType) ?: false
    val isArray = starProj?.isAssignableFrom(arrayType) ?: false

    if (isList || isArray) {
        "%s?.filterNotNull()?.forEach { %s.appendAll(\"\$it\", emptyList()) }\n".format(
            name,
            if (encoded) {
                "encodedParameters"
            } else {
                "parameters"
            }
        )
    } else {
        "%s.appendAll(\"\$%s\", emptyList())\n"
            .format(
                if (encoded) {
                    "encodedParameters"
                } else {
                    "parameters"
                },
                name
            )
    }
}

private fun getQueryText(
    params: List<ParameterData>,
    listType: KSType,
    arrayType: KSType
) = params.filter { it.hasAnnotation<Query>() }.joinToString(separator = "") { parameterData ->
    val query = parameterData.annotations.filterIsInstance<Query>().first()
    val encoded = query.encoded
    val starProj = parameterData.type.parameterType?.resolve()?.starProjection()
    val isList = starProj?.isAssignableFrom(listType) ?: false
    val isArray = starProj?.isAssignableFrom(arrayType) ?: false

    if (isList || isArray) {
        "%s?.filterNotNull()?.forEach { %s(\"%s\", \"\$it\") }\n".format(
            parameterData.name,
            if (encoded) {
                "encodedParameters.append"
            } else {
                "parameter"
            },
            query.value
        )
    } else {
        if (encoded) {
            "%s?.let{ encodedParameters.append(\"%s\", \"\$it\") }\n".format(parameterData.name, query.value)
        } else {
            "%s?.let{ parameter(\"%s\", \"\$it\") }\n".format(parameterData.name, query.value)
        }
    }

}