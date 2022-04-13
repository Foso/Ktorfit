package de.jensklingenberg.ktorfit.model

import de.jensklingenberg.ktorfit.model.annotations.ParamAnnotation

data class MyParam(
    val name: String,
    val type: MyType,
    val annotations: List<ParamAnnotation> = emptyList(),
    val hasRequestBuilderAnno: Boolean = false
)