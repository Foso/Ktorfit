package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.*
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty


fun getPartsCode(params: List<ParameterData>, listType: KSType, arrayType: KSType): String {

    val text = params.filter { it.hasAnnotation<Part>() }.joinToString("") { parameterData ->
        val field = parameterData.annotations.filterIsInstance<Part>().first()
        val data = parameterData.name
        val fieldKey = field.value

        val starProj = parameterData.type.parameterType?.resolve()?.starProjection()
        val isList = starProj?.isAssignableFrom(listType) ?: false
        val isArray = starProj?.isAssignableFrom(arrayType) ?: false

        when {
            isList || isArray -> {
                "$data?.filterNotNull()?.forEach { append(\"$fieldKey\", \"\${it}\") }\n"
            }

            else -> {
                "$data?.let{ append(\"$fieldKey\", \"\${it}\") }\n"
            }
        }
    }

    val fieldMapStrings = params.filter { it.hasAnnotation<PartMap>() }.joinToString("") { parameterData ->

        "${parameterData.name}?.forEach { entry -> entry.value?.let{ append(entry.key, \"\${entry.value}\") } }\n"
    }

    return (text + fieldMapStrings).surroundIfNotEmpty(
        "val _formData = formData {\n", "}\nsetBody(MultiPartFormDataContent(_formData))\n"
    )
}

