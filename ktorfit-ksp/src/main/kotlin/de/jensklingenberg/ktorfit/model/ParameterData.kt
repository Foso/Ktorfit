package de.jensklingenberg.ktorfit.model

import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation

data class ParameterData(
    val name: String,
    val type: TypeData,
    val annotations: List<ParameterAnnotation> = emptyList(),
    val hasRequestBuilderAnno: Boolean = false
) {
    inline fun <reified T> findAnnotationOrNull(): T? {
        return this.annotations.firstOrNull { it is T } as? T
    }

    inline fun <reified T> hasAnnotation(): Boolean {
        return this.findAnnotationOrNull<T>() != null
    }

}