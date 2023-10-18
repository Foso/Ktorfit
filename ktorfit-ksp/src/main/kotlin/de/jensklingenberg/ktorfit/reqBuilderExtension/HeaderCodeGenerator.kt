package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.*
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.*
import de.jensklingenberg.ktorfit.utils.anyInstance
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty


/**
 * This will generate the source for headers{...} that will be used in the HttpRequestBuilder extension
 */
fun getHeadersCode(
    functionAnnotations: List<FunctionAnnotation>,
    parameterDataList: List<ParameterData>,
    listType: KSType,
    arrayType: KSType
): String {

    val headerAnnotationText =
        parameterDataList
            .filter { it.hasAnnotation<Header>() }
            .joinToString("") { parameterData ->
                val paramName = parameterData.name

                val starProj = parameterData.type.parameterType?.resolve()?.starProjection()
                val isList = starProj?.isAssignableFrom(listType) ?: false
                val isArray = starProj?.isAssignableFrom(arrayType) ?: false

                val headerName = parameterData.findAnnotationOrNull<Header>()?.path.orEmpty()
                val isStringType = parameterData.type.qualifiedName == "kotlin.String"

                when {
                    isList || isArray -> {
                        val hasNullableInnerType = parameterData.type.innerTypeName.endsWith("?")
                        val isStringListOrArray = when (parameterData.type.innerTypeName) {
                            "String", "String?" -> true
                            else -> false
                        }
                        val headerListStringBuilder = StringBuilder()

                        headerListStringBuilder.append(
                            if (parameterData.type.isNullable) {
                                "$paramName?"
                            } else {
                                paramName
                            }
                        )

                        if (hasNullableInnerType) {
                            headerListStringBuilder.append(
                                if (parameterData.type.isNullable) {
                                    ".filterNotNull()?"
                                } else {
                                    ".filterNotNull()"
                                }
                            )
                        }
                        headerListStringBuilder.append(".forEach{ append(\"$headerName\", ")
                        headerListStringBuilder.append(
                            if (isStringListOrArray) {
                                "it"
                            } else {
                                "\"\$it\""
                            }
                        )
                        headerListStringBuilder.append(")}\n")

                        headerListStringBuilder.toString()
                    }

                    else -> {
                        val headerValue =
                            if (isStringType) paramName else "\"\$$paramName\""

                        if (parameterData.type.isNullable) {
                            "%s?.let{ append(\"%s\", %s) }\n".format(
                                paramName,
                                headerName,
                                headerValue
                            )
                        } else {
                            "append(\"%s\", %s)\n".format(headerName, headerValue)
                        }
                    }
                }
            }

    val headersAnnotationText = functionAnnotations
        .firstNotNullOfOrNull { it as? Headers }
        ?.value
        ?.joinToString("") {
            val (key, value) = it.split(":")
            "append(\"%s\", \"%s\")\n".format(key.trim(), value.trim())
        }.orEmpty()

    val headerMapAnnotationText = parameterDataList
        .filter { it.hasAnnotation<HeaderMap>() }
        .joinToString("") {
            val mapValueType = it.type.innerTypeName.split(",")[1].trim()
            val valueIsString = (mapValueType == "String" || mapValueType == "String?")

            val headerMapStringBuilder = StringBuilder()
            headerMapStringBuilder.append(
                if (it.type.isNullable) {
                    "${it.name}?"
                } else {
                    it.name
                }
            )
            headerMapStringBuilder.append(".forEach{")
            val hasNullableKeyType = mapValueType.endsWith("?")
            headerMapStringBuilder.append(
                if (hasNullableKeyType) {
                    "it.value?.let{ value -> "
                } else {
                    ""
                }
            )

            headerMapStringBuilder.append(" append(it.key , ")
            headerMapStringBuilder.append(
                if (valueIsString && hasNullableKeyType) {
                    "value) }"
                } else if (valueIsString && !hasNullableKeyType) {
                    "it.value)"
                } else if (hasNullableKeyType) {
                    "\"\$value\") }"
                } else {
                    "\"\${it.value}\")"
                }
            )
            headerMapStringBuilder.append("}\n")
            headerMapStringBuilder.toString()
        }

    val contentTypeText = if (functionAnnotations.anyInstance<FormUrlEncoded>()) {
        "append(\"Content-Type\", \"application/x-www-form-urlencoded\")\n"
    } else ""

    return "$contentTypeText$headerAnnotationText$headerMapAnnotationText$headersAnnotationText".surroundIfNotEmpty(
        "headers{\n",
        "}"
    )
}