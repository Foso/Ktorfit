package de.jensklingenberg.ktorfit.utils

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName

fun AnnotationSpec.toClassName(): ClassName {
    return if (typeName is ClassName) {
        typeName as ClassName
    } else {
        (typeName as ParameterizedTypeName).rawType
    }
}
