package de.jensklingenberg.ktorfit.reqBuilderExtension

import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation
import de.jensklingenberg.ktorfit.model.annotationsAttributeKey
import de.jensklingenberg.ktorfit.model.pathTemplateAttributeKey
import de.jensklingenberg.ktorfit.utils.toClassName

fun getAttributesCode(
    functionData: FunctionData
): String {
    val templatePath = functionData.httpMethodAnnotation.path
    val parameterAttributes =
        "setAttributes {\n" +
            "put(AttributeKey(\"${pathTemplateAttributeKey.objectName}\"), \"${templatePath}\")\n" +
            functionData.parameterDataList
                .filter { it.hasAnnotation<ParameterAnnotation.Tag>() }
                .joinToString("\n") {
                    val tag =
                        it.findAnnotationOrNull<ParameterAnnotation.Tag>()
                            ?: throw IllegalStateException("Tag annotation not found")
                    if (it.type.parameterType.isMarkedNullable) {
                        "${it.name}?.let{ put(AttributeKey(\"${tag.value}\"), it) }"
                    } else {
                        "put(AttributeKey(\"${tag.value}\"), ${it.name})"
                    }
                } + "}"

    if (functionData.nonKtorfitAnnotations.isEmpty()) return parameterAttributes

    val annotationsAttribute =
        functionData.nonKtorfitAnnotations
            .joinToString(
                separator = ",\n",
                prefix = "listOf(\n",
                postfix = ",\n)",
            ) { annotation ->
                annotation
                    .members
                    .joinToString {
                        it.toString().replace("@", "")
                    }.let { "${annotation.toClassName().simpleName}($it)" }
            }.let { "attributes.put(${annotationsAttributeKey.objectName}, $it)" }

    return if (parameterAttributes.isNotEmpty()) {
        parameterAttributes + "\n" + annotationsAttribute
    } else {
        annotationsAttribute
    }
}
