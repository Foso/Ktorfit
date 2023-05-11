package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.Field
import de.jensklingenberg.ktorfit.model.annotations.FieldMap
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty


fun getFieldArgumentsText(params: List<ParameterData>): String {
    //Get all Parameter with @Field and add them to a list

    val text = params.filter { it.hasAnnotation<Field>() }.joinToString("") { parameterData ->
        val field = parameterData.annotations.filterIsInstance<Field>().first()
        val encoded = field.encoded
        val data = parameterData.name
        val fieldKey = field.value

        "$data?.let{ append(\"$fieldKey\", \"\${it}\"%s) }\n".format(
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
            }
        )

    }

    val fieldMapStrings = params.filter { it.hasAnnotation<FieldMap>() }.joinToString("") { parameterData ->
        val fieldMap = parameterData.findAnnotationOrNull<FieldMap>()!!
        val encoded = fieldMap.encoded
        val data = parameterData.name

        "%s?.forEach { entry -> entry.value?.let{ append(entry.key, \"\${entry.value}%s\") } }\n".format(
            data,
            if (encoded) {
                ".decodeURLQueryComponent(plusIsSpace = true)"
            } else {
                ""
            }
        )
    }

    return (text + fieldMapStrings).surroundIfNotEmpty(
        "val _formParameters = Parameters.build {\n", "}\nsetBody(FormDataContent(_formParameters))\n"
    )
}

