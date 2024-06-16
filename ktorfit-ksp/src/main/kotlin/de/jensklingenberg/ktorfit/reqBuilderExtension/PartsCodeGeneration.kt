package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Part
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.PartMap
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty

fun getPartsCode(
    params: List<ParameterData>,
    listType: KSType,
    arrayType: KSType,
): String {
    val partText =
        params.filter { it.hasAnnotation<Part>() }.joinToString("") { parameterData ->
            val part = parameterData.annotations.filterIsInstance<Part>().first()
            val name = parameterData.name
            val partValue = part.value

            val starProj = parameterData.type.parameterType.starProjection()
            val isList = starProj.isAssignableFrom(listType)
            val isArray = starProj.isAssignableFrom(arrayType)

            when {
                isList || isArray -> {
                    "$name?.filterNotNull()?.forEach { append(\"$partValue\", \"\${it}\") }\n"
                }

                else -> {
                    "$name?.let{ append(\"$partValue\", \"\${it}\") }\n"
                }
            }
        }

    val partMapStrings =
        params.filter { it.hasAnnotation<PartMap>() }.joinToString("") { parameterData ->
            "${parameterData.name}?.forEach { entry -> entry.value?.let{ append(entry.key, \"\${entry.value}\") } }\n"
        }

    return (partText + partMapStrings).surroundIfNotEmpty(
        "val __formData = formData {\n",
        "}\nsetBody(MultiPartFormDataContent(__formData))\n",
    )
}
