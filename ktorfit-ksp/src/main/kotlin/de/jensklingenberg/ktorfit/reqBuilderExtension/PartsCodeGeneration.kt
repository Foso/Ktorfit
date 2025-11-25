package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Part
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.PartMap
import de.jensklingenberg.ktorfit.model.formData
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty

fun getPartsCode(
    params: List<ParameterData>,
    listType: KSType,
    arrayType: KSType,
): String {
    val partTextList =
        params.filter { it.hasAnnotation<Part>() }.joinToString("") { parameterData ->
            val part =
                parameterData.annotations
                    .filterIsInstance<Part>()
                    .firstOrNull() ?: throw IllegalStateException("Part annotation not found")
            val name = parameterData.name
            val partValue = part.value

            val starProj = parameterData.type.parameterType?.starProjection()
            val isList = starProj?.isAssignableFrom(listType) ?: false
            val isArray = starProj?.isAssignableFrom(arrayType) ?: false

            when {
                isList || isArray -> {
                    name
                }

                else -> {
                    ""
                }
            }
        }

    val partText =
        params.filter { it.hasAnnotation<Part>() }.joinToString("") { parameterData ->
            val part =
                parameterData.annotations
                    .filterIsInstance<Part>()
                    .firstOrNull() ?: throw IllegalStateException("Part annotation not found")
            val name = parameterData.name
            val partValue = part.value

            val starProj = parameterData.type.parameterType?.starProjection()
            val isList = starProj?.isAssignableFrom(listType) ?: false
            val isArray = starProj?.isAssignableFrom(arrayType) ?: false

            when {
                isList || isArray -> {
                    ""
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

    val code = (partText + partMapStrings).surroundIfNotEmpty(
        "val ${formData.objectName} = formData {\n",
        "}",
    )

    return if(code.isNotEmpty()){
        code + " + ${partTextList}\n  setBody(MultiPartFormDataContent(${formData.objectName}))\n"
    }else if (partTextList.isNotEmpty()){
        "setBody(MultiPartFormDataContent(${partTextList}))\n"
    }else{
        ""
    }

}
