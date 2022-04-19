package de.jensklingenberg.ktorfit.model

import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation

data class ParameterData(
    val name: String,
    val type: TypeData,
    val annotations: List<ParameterAnnotation> = emptyList(),
    val hasRequestBuilderAnno: Boolean = false
)