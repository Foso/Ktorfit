package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.FormUrlEncoded
import de.jensklingenberg.ktorfit.model.annotations.FunctionAnnotation
import de.jensklingenberg.ktorfit.model.annotations.Headers
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Header
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.HeaderMap
import de.jensklingenberg.ktorfit.utils.anyInstance
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty

/**
 * This will generate the source for headers{...} that will be used in the HttpRequestBuilder extension
 */
fun getHeadersCode(
    functionAnnotations: List<FunctionAnnotation>,
    parameterDataList: List<ParameterData>,
    listType: KSType,
    arrayType: KSType,
): String {
    val headersAnnotationText =
        functionAnnotations
            .firstNotNullOfOrNull { it as? Headers }
            ?.value
            ?.joinToString("") {
                val (key, value) = it.split(":")
                "append(\"%s\", \"%s\")\n".format(key.trim(), value.trim())
            }.orEmpty()

    val contentTypeText =
        if (functionAnnotations.anyInstance<FormUrlEncoded>()) {
            "append(\"Content-Type\", \"application/x-www-form-urlencoded\")\n"
        } else {
            ""
        }

    return "$contentTypeText${headerAnnotationText(parameterDataList, listType, arrayType)}${headerMapAnnotationText(parameterDataList)}$headersAnnotationText".surroundIfNotEmpty(
        "headers{\n",
        "}",
    )
}

private fun headerAnnotationText(
    parameterDataList: List<ParameterData>,
    listType: KSType,
    arrayType: KSType
): String =
    parameterDataList
        .filter { it.hasAnnotation<Header>() }
        .joinToString("") { parameterData ->
            val paramName = parameterData.name

            val starProj = parameterData.type.parameterType?.starProjection()
            val isList = starProj?.isAssignableFrom(listType) ?: false
            val isArray = starProj?.isAssignableFrom(arrayType) ?: false

            val headerName = parameterData.findAnnotationOrNull<Header>()?.path.orEmpty()

            val isStringType =
                (
                    parameterData.type.parameterType
                        .toTypeName()
                        .toString() == "kotlin.String"
                ) || (
                    parameterData.type.parameterType
                        .toTypeName()
                        .toString() == "kotlin.String?"
                )

            return@joinToString if (isList || isArray) {
                resolveIfIsListOrArray(parameterData, paramName, headerName)
            } else {
                val headerValue =
                    if (isStringType) paramName else "\"\$$paramName\""

                if (parameterData.type.parameterType.isMarkedNullable) {
                    "%s?.let{ append(\"%s\", %s) }\n".format(
                        paramName,
                        headerName,
                        headerValue,
                    )
                } else {
                    "append(\"%s\", %s)\n".format(headerName, headerValue)
                }
            }
        }

private fun resolveIfIsListOrArray(
    parameterData: ParameterData,
    paramName: String,
    headerName: String
): String {
    val innerType =
        (
            parameterData
                .type
                .parameterType
                .toTypeName() as? ParameterizedTypeName
        )?.typeArguments
            ?.joinToString { it.toString() }
            .orEmpty()
    val hasNullableInnerType =
        innerType.endsWith("?")
    val isStringListOrArray =
        when (
            (
                parameterData
                    .type
                    .parameterType
                    .toTypeName() as? ParameterizedTypeName
            )?.typeArguments
                ?.joinToString { it.toString() }
                .orEmpty()
        ) {
            "kotlin.String", "kotlin.String?" -> true
            else -> false
        }
    val headerListStringBuilder = StringBuilder()

    headerListStringBuilder.append(
        if (parameterData.type.parameterType.isMarkedNullable) {
            "$paramName?"
        } else {
            paramName
        },
    )

    if (hasNullableInnerType) {
        headerListStringBuilder.append(
            ".filterNotNull()" +
                if (parameterData.type.parameterType.isMarkedNullable) {
                    "?"
                } else {
                    ""
                },
        )
    }
    headerListStringBuilder.append(".forEach{ append(\"$headerName\", ")
    headerListStringBuilder.append(
        if (isStringListOrArray) {
            "it"
        } else {
            "\"\$it\""
        },
    )
    headerListStringBuilder.append(")}\n")

    return headerListStringBuilder.toString()
}

private fun headerMapAnnotationText(parameterDataList: List<ParameterData>): String =
    parameterDataList
        .filter { it.hasAnnotation<HeaderMap>() }
        .joinToString("") { parameterData ->
            val mapValueType =
                (parameterData.type.parameterType.toTypeName() as? ParameterizedTypeName)
                    ?.typeArguments
                    ?.joinToString { it.toString() }
                    .orEmpty()
                    .split(",")
                    .getOrNull(1)
                    ?.trim()
                    .orEmpty()
            val valueIsString = (mapValueType == "kotlin.String" || mapValueType == "kotlin.String?")

            val headerMapStringBuilder = StringBuilder()
            headerMapStringBuilder.append(
                if (parameterData.type.parameterType.isMarkedNullable) {
                    "${parameterData.name}?"
                } else {
                    parameterData.name
                },
            )
            headerMapStringBuilder.append(".forEach{")
            val hasNullableKeyType = mapValueType.endsWith("?")
            headerMapStringBuilder.append(
                if (hasNullableKeyType) {
                    "it.value?.let{ value -> "
                } else {
                    ""
                },
            )

            headerMapStringBuilder.append(" append(it.key , ")
            headerMapStringBuilder.append(
                when {
                    valueIsString && hasNullableKeyType -> {
                        "value) }"
                    }
                    valueIsString && !hasNullableKeyType -> {
                        "it.value)"
                    }
                    hasNullableKeyType -> {
                        "\"\$value\") }"
                    }
                    else -> {
                        "\"\${it.value}\")"
                    }
                },
            )
            headerMapStringBuilder.append("}\n")
            headerMapStringBuilder.toString()
        }
