package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.squareup.kotlinpoet.AnnotationSpec
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation
import de.jensklingenberg.ktorfit.model.annotationsAttributeKey

fun getAttributesCode(
    parameterDataList: List<ParameterData>,
    annotations: List<AnnotationSpec>,
): String {
    val parameterAttributes =
        parameterDataList
            .filter { it.hasAnnotation<ParameterAnnotation.Tag>() }
            .joinToString("\n") {
                val tag =
                    it.findAnnotationOrNull<ParameterAnnotation.Tag>()
                        ?: throw IllegalStateException("Tag annotation not found")
                if (it.type.parameterType.isMarkedNullable) {
                    "${it.name}?.let{ attributes.put(AttributeKey(\"${tag.value}\"), it) }"
                } else {
                    "attributes.put(AttributeKey(\"${tag.value}\"), ${it.name})"
                }
            }

    if (annotations.isEmpty()) return parameterAttributes

    val annotationsAttribute =
        annotations.joinToString(
            separator = ",\n",
            prefix = "listOf(\n",
            postfix = ",\n)",
        ) { annotation ->
            annotation.toString().trimStart('@')
        }
            .let { "attributes.put(${annotationsAttributeKey.name}, $it)" }

    return if (parameterAttributes.isNotEmpty()) {
        parameterAttributes + "\n" + annotationsAttribute
    } else {
        annotationsAttribute
    }
}
