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

                val headerName = parameterData.findAnnotationOrNull<Header>()?.path ?: ""
                val isStringType = parameterData.type.qualifiedName == "kotlin.String"

                when {
                    isList || isArray -> {
                        val hasNullableInnerType = parameterData.type.innerTypeName.endsWith("?")
                        val isStringList = when (parameterData.type.innerTypeName) {
                            "String", "String?" -> true
                            else -> false
                        }
                        var text: String = paramName+ if (parameterData.type.isNullable) {
                            "?"
                        } else {
                            ""
                        }
                        text += if (hasNullableInnerType) {
                            ".filterNotNull()" + ("?".takeIf { parameterData.type.isNullable }
                                ?: "")
                        } else {
                            ""
                        }
                        text += ".forEach{ append(\"$headerName\"," + if (isStringList) {
                            " it "
                        } else {
                            " \"\$it\""
                        } + ")}\n"
                        
                        text
                    }

                    else -> {
                        if (parameterData.type.isNullable) {
                            val headerValue =
                                if (isStringType) paramName else "\$$paramName.toString()"
                            "$paramName?.let{ append(\"$headerName\", $headerValue) }\n"
                        } else {
                            val headerValue =
                                if (isStringType) paramName else "\$$paramName.toString()"
                            "append(\"$headerName\", $headerValue) \n"
                        }
                    }
                }
            }

    val headersAnnotationText = functionAnnotations
        .filterIsInstance<Headers>()
        .firstOrNull()
        ?.value
        ?.joinToString("") {
            val (key, value) = it.split(":")
            "append(\"%s\", \"%s\")\n".format(key, value.trim())
        } ?: ""

    val headerMapAnnotationText = parameterDataList
        .filter { it.hasAnnotation<HeaderMap>() }
        .joinToString("") {
            "${it.name}?.forEach { append(it.key, \"\${it.value}\") }\n"
        }

    val contentTypeText = if (functionAnnotations.anyInstance<FormUrlEncoded>()) {
        "append(\"Content-Type\", \"application/x-www-form-urlencoded\")\n"
    } else ""

    return "$contentTypeText$headerAnnotationText$headerMapAnnotationText$headersAnnotationText".surroundIfNotEmpty(
        "headers{\n",
        "}"
    )
}