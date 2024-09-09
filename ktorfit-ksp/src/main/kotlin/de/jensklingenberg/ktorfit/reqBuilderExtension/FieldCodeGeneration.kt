package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Field
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.FieldMap
import de.jensklingenberg.ktorfit.model.formParameters
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty

fun getFieldArgumentsText(
    params: List<ParameterData>,
    listType: KSType,
    arrayType: KSType,
): String {
    val fieldText =
        params.filter { it.hasAnnotation<Field>() }.joinToString("") { parameterData ->
            val field =
                parameterData.annotations.filterIsInstance<Field>().firstOrNull()
                    ?: throw IllegalStateException("Field annotation not found")
            val encoded = field.encoded
            val paramName = parameterData.name
            val fieldValue = field.value
            val starProj = parameterData.type.parameterType?.starProjection()

            val isList = starProj?.isAssignableFrom(listType) ?: false
            val isArray = starProj?.isAssignableFrom(arrayType) ?: false

            when {
                isList || isArray -> {
                    "$paramName?.filterNotNull()?.forEach { append(\"$fieldValue\", \"\$it\"%s) }\n".format(
                        if (encoded) {
                            /**
                             * This is a workaround.
                             * Ktor encodes parameters by default and I don't know
                             * how to deactivate this.
                             * When the value is not encoded it will be given to Ktor unchanged.
                             * If it is encoded, it gets decoded, so Ktor can encode it again.
                             */
                            ".decodeURLQueryComponent(plusIsSpace = true)"
                        } else {
                            ""
                        },
                    )
                }

                else -> {
                    "$paramName?.let{ append(\"$fieldValue\", \"\${it}\"%s) }\n".format(
                        if (encoded) {
                            ".decodeURLQueryComponent(plusIsSpace = true)"
                        } else {
                            ""
                        },
                    )
                }
            }
        }

    val fieldMapStrings =
        params
            .filter { it.hasAnnotation<FieldMap>() }
            .joinToString("") { parameterData ->

                val fieldMap =
                    parameterData.findAnnotationOrNull<FieldMap>()
                        ?: throw IllegalStateException("FieldMap annotation not found")
                val encoded = fieldMap.encoded
                val data = parameterData.name

                "%s?.forEach { entry -> entry.value?.let{ append(entry.key, \"\${entry.value}%s\") } }\n".format(
                    data,
                    if (encoded) {
                        ".decodeURLQueryComponent(plusIsSpace = true)"
                    } else {
                        ""
                    },
                )
            }

    return (fieldText + fieldMapStrings).surroundIfNotEmpty(
        "val ${formParameters.objectName} = Parameters.build {\n",
        "}\nsetBody(FormDataContent(${formParameters.objectName}))\n",
    )
}
