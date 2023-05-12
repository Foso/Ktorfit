package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.*
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

    val contentTypeText = if (functionAnnotations.anyInstance<FormUrlEncoded>()) {
        "append(\"Content-Type\", \"application/x-www-form-urlencoded\")\n"
    } else ""

    val headerAnnotationText =
        parameterDataList
            .filter { it.hasAnnotation<Header>() }
            .joinToString("") { parameterData ->
                val paramName = parameterData.name

                val starProj = parameterData.type.parameterType?.resolve()?.starProjection()
                val isList = starProj?.isAssignableFrom(listType) ?: false
                val isArray = starProj?.isAssignableFrom(arrayType) ?: false

                val headerName = parameterData.findAnnotationOrNull<Header>()?.path ?: ""

                when {
                    isList || isArray -> {
                        "%s?.filterNotNull()?.forEach { append(\"%s\", \"\$it\") }\n".format(paramName, headerName)
                    }

                    else -> {
                        "$paramName?.let{ append(\"$headerName\", \"\$it\") }\n"
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

    return "$contentTypeText$headerAnnotationText$headerMapAnnotationText$headersAnnotationText".surroundIfNotEmpty(
        "headers{\n",
        "}"
    )
}