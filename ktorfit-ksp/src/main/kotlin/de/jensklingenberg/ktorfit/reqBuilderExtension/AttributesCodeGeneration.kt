package de.jensklingenberg.ktorfit.reqBuilderExtension

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import de.jensklingenberg.ktorfit.model.annotations.CustomHttp
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotationsAttributeKey
import de.jensklingenberg.ktorfit.utils.toClassName

fun getAttributesCode(rawAnnotation: List<AnnotationSpec>): String {
    val annotations = rawAnnotation.joinToString(
        separator = ",\n",
        prefix = "listOf(\n",
        postfix = ",\n)",
    ) { annotation ->
        annotation
            .members
            .joinToString { it.toString() }
            .let { "${annotation.toClassName().simpleName}($it)" }
    }

    return "attributes.put(${annotationsAttributeKey.name}, $annotations)"
}
