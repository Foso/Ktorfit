package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation

fun getAttributeCode(parameterDataList: List<ParameterData>): String {
    return parameterDataList.filter { it.hasAnnotation<ParameterAnnotation.Tag>() }
        .joinToString("\n") {
            val tag = it.findAnnotationOrNull<ParameterAnnotation.Tag>()!!
            if (it.type.isNullable) {
                "${it.name}?.let{ attributes.put(io.ktor.util.AttributeKey(\"${tag.value}\"), it) }"
            } else {
                "attributes.put(io.ktor.util.AttributeKey(\"${tag.value}\"), ${it.name})"
            }
        }
}

