package de.jensklingenberg.ktorfit.model

import de.jensklingenberg.ktorfit.model.annotations.FunctionAnnotation
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation

data class FunctionData(
    val name: String,
    val returnType: TypeData,
    val isSuspend: Boolean = false,
    val params: List<ParameterData>,
    val annotations: List<FunctionAnnotation> = emptyList(),
    val httpMethodAnnotation: HttpMethodAnnotation
)