package de.jensklingenberg.ktorfit.reqBuilderExtension


import KtorfitProcessor
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.*

/**
 * This will generate the code for the HttpRequestBuilder
 */
@OptIn(KspExperimental::class)
fun getReqBuilderExtensionText(functionData: FunctionData): String {
    val methodAnnotation = functionData.httpMethodAnnotation
    //METHOD
    val httpMethodValue = if (methodAnnotation is CustomHttp) {
        methodAnnotation.customValue
    } else {
        methodAnnotation.httpMethod.keyword
    }
    val method = "this.method = HttpMethod.parse(\"${httpMethodValue}\")"
    val headers = getHeadersCode(
        functionData.annotations,
        functionData.parameterDataList,
        KtorfitProcessor.ktorfitResolver.getKotlinClassByName("kotlin.collections.List")?.asStarProjectedType(),
        KtorfitProcessor.ktorfitResolver.builtIns.arrayType.starProjection()
    )

    val param = getQueryArgumentText2(
        functionData.parameterDataList,
        KtorfitProcessor.ktorfitResolver.getKotlinClassByName("kotlin.collections.List")?.asStarProjectedType(),
        KtorfitProcessor.ktorfitResolver.builtIns.arrayType.starProjection()
    )
    val body = getBodyDataText(functionData.parameterDataList)

    val args = listOf(
        method,
        body,
        headers,
        param
    ).filter { it.isNotEmpty() }
        .joinToString("\n") { it }

    return "val _ext: HttpRequestBuilder.() -> Unit = {\n$args \n}"
}

fun getBodyDataText(params: List<ParameterData>): String {
    return params.firstOrNull { it.hasAnnotation<Body>() }?.let {
        "setBody(%s)".format(it.name)
    } ?: ""
}


fun getQueryArgumentText2(params: List<ParameterData>, listType: KSType?, arrayType: KSType): String {
    //Get all Parameter with @Query and add them to a list

    val queryText = params.filter { it.hasAnnotation<Query>() }.joinToString(separator = "") { parameterData ->
        val query = parameterData.annotations.filterIsInstance<Query>().first()
        val encoded = query.encoded
        val starProj = parameterData.type.parameterType?.resolve()?.starProjection()
        val isList = starProj?.isAssignableFrom(listType!!) ?: false
        val isArray = starProj?.isAssignableFrom(arrayType) ?: false

        if (isList || isArray) {
            "%s?.filterNotNull()?.forEach { %s(\"%s\", it.toString()) }\n".format(
                parameterData.name,
                if (encoded) {
                    "url.encodedParameters.append"
                } else {
                    "parameter"
                },
                query.value
            )
        } else {
            if (encoded) {
                "url.encodedParameters.append(\"%s\", %s)".format(query.value, parameterData.name)
            } else {
                "parameter(\"%s\", %s)".format(query.value, parameterData.name)
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
            "%s?.filterNotNull()?.forEach { url.%s.appendAll(it, emptyList()) }\n".format(
                parameterData.name,
                if (encoded) {
                    "encodedParameters"
                } else {
                    "parameters"
                }
            )
        } else {
            if (encoded) {
                "url.encodedParameters.appendAll(\"$data\", emptyList())\n"
            } else {
                "url.parameters.appendAll(\"$data\", emptyList())\n"
            }
        }
    }

    val queryMapStrings = params.filter { it.hasAnnotation<QueryMap>() }.joinToString(separator = "") { myParam ->
        val queryMap = myParam.findAnnotationOrNull<QueryMap>()!!
        val encoded = queryMap.encoded
        val data = myParam.name
        "%s?.forEach { entry -> entry.value?.let{ %s(entry.key, entry.value.toString()) } }\n".format(
            data,
            if (encoded) {
                "url.encodedParameters.append"
            } else {
                "parameter"
            }
        )

    }

    return queryText + queryNameText +queryMapStrings//myQueryStrings.joinToString { it.toString() }.surroundIfNotEmpty("queries = listOf(", ")")
}