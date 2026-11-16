package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.google.devtools.ksp.symbol.KSType
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Part
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.PartMap
import de.jensklingenberg.ktorfit.model.formData
import de.jensklingenberg.ktorfit.utils.resolveTypeAlias
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty

fun getPartsCode(
    params: List<ParameterData>,
    listType: KSType,
    arrayType: KSType,
): String {
    val rawParts = mutableListOf<String>()
    val partText =
        params.filter { it.hasAnnotation<Part>() }.joinToString("") { parameterData ->
            val part =
                parameterData.annotations
                    .filterIsInstance<Part>()
                    .firstOrNull() ?: throw IllegalStateException("Part annotation not found")
            val name = parameterData.name
            val partValue = part.value

            val parameterType = parameterData.type.parameterType.resolveTypeAlias()
            val starProj = parameterType.starProjection()
            val isList = starProj.isAssignableFrom(listType)
            val isArray = starProj.isAssignableFrom(arrayType)
            val isPartDataCollection =
                (isList || isArray) &&
                    parameterType.arguments
                        .firstOrNull()
                        ?.type
                        ?.resolve()
                        ?.resolveTypeAlias()
                        ?.declaration
                        ?.qualifiedName
                        ?.asString() == "io.ktor.http.content.PartData"

            when {
                isPartDataCollection -> {
                    rawParts.add("$name.filterNotNull()")
                    ""
                }

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

    val formParts = (partText + partMapStrings).surroundIfNotEmpty("formData {\n", "}")
    val allParts = (listOf(formParts) + rawParts).filter { it.isNotEmpty() }.joinToString(" + ")

    return allParts.surroundIfNotEmpty(
        "val ${formData.objectName} = ",
        "\nsetBody(MultiPartFormDataContent(${formData.objectName}))\n",
    )
}
