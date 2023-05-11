package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Query
import de.jensklingenberg.ktorfit.model.annotations.QueryMap
import de.jensklingenberg.ktorfit.model.annotations.QueryName
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty

fun getQueryCode(params: List<ParameterData>, listType: KSType?, arrayType: KSType): String {
    //Get all Parameter with @Query and add them to a list

    val queryText = params.filter { it.hasAnnotation<Query>() }.joinToString(separator = "") { parameterData ->
        val query = parameterData.annotations.filterIsInstance<Query>().first()
        val encoded = query.encoded
        val starProj = parameterData.type.parameterType?.resolve()?.starProjection()
        val isList = starProj?.isAssignableFrom(listType!!) ?: false
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
                "%s?.let{ encodedParameters.append(\"%s\", \"\$it\") }\n".format(parameterData.name,query.value)
            } else {
                "%s?.let{ parameter(\"%s\", \"\$it\") }\n".format(parameterData.name, query.value)
            }
        }

    }


    val queryNameText = params.filter { it.hasAnnotation<QueryName>() }.joinToString(separator = "") { parameterData ->
        val queryName = parameterData.annotations.filterIsInstance<QueryName>().first()
        val encoded = queryName.encoded
        val data = parameterData.name

        val starProj = parameterData.type.parameterType?.resolve()?.starProjection()
        val isList = starProj?.isAssignableFrom(listType!!) ?: false
        val isArray = starProj?.isAssignableFrom(arrayType) ?: false

        if (isList || isArray) {
            "%s?.filterNotNull()?.forEach { %s.appendAll(\"\$it\", emptyList()) }\n".format(
                parameterData.name,
                if (encoded) {
                    "encodedParameters"
                } else {
                    "parameters"
                }
            )
        } else {/**/
            "%s.appendAll(\"\$%s\", emptyList())\n"
                .format(
                    if (encoded) {
                        "encodedParameters"
                    } else {
                        "parameters"
                    },
                    data
                )
        }
    }

    val queryMapStrings = params.filter { it.hasAnnotation<QueryMap>() }.joinToString(separator = "") { myParam ->
        val queryMap = myParam.findAnnotationOrNull<QueryMap>()!!
        val encoded = queryMap.encoded
        val data = myParam.name
        "%s?.forEach { entry -> entry.value?.let{ %s(entry.key, \"\${entry.value}\") } }\n".format(
            data,
            if (encoded) {
                "encodedParameters.append"
            } else {
                "parameter"
            }
        )

    }

    return (queryText + queryNameText + queryMapStrings).surroundIfNotEmpty(
        "url{\n",
        "}"
    )//myQueryStrings.joinToString { it.toString() }.surroundIfNotEmpty("queries = listOf(", ")")
}