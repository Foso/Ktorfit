package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.*
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.*
import de.jensklingenberg.ktorfit.utils.anyInstance
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty


/**
 * Source for the "headers" argument of [de.jensklingenberg.ktorfit.RequestData]
 */

fun getHeadersCode(
    functionAnnotations: List<FunctionAnnotation>,
    parameterDataList: List<ParameterData>,
    listType: KSType?,
    arrayType: KSType
): String {

    val contentTypeText = if (functionAnnotations.anyInstance<FormUrlEncoded>()) {
        "append(\"Content-Type\", \"application/x-www-form-urlencoded\")\n"
    } else ""

    val headerAnnotationText = parameterDataList.filter { it.hasAnnotation<Header>() }.joinToString("\n") { parameterData ->
        val paramName = parameterData.name

        val starProj = parameterData.type.parameterType?.resolve()?.starProjection()
        val isList = starProj?.isAssignableFrom(listType!!) ?: false
        val isArray =
            starProj?.isAssignableFrom(arrayType) ?: false

        val headerPath = parameterData.findAnnotationOrNull<Header>()?.path ?: ""

        when {
            isList || isArray -> {
                "${paramName}?.filterNotNull()?.forEach { append(\"${headerPath}\", it.toString()) }"
            }

            else -> {
                "append(\"${headerPath}\", ${paramName}.toString())"
            }
        }
    }

    val headersAnnotationText = functionAnnotations.filterIsInstance<Headers>().firstOrNull()?.path?.joinToString("") {
        val (key, value) = it.split(":")
        "append(\"$key\", \"${value.trim()}\")\n"
    } ?: ""

    val headerMapAnnotationText = parameterDataList.filter { it.hasAnnotation<HeaderMap>() }.joinToString("") {
        "${it.name}?.forEach { append(it.key, it.value.toString()) }\n"
    }

    return "$contentTypeText$headerAnnotationText$headerMapAnnotationText$headersAnnotationText".surroundIfNotEmpty("headers{\n", "}")
}