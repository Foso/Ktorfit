package de.jensklingenberg.ktorfit.model

import de.jensklingenberg.ktorfit.model.annotations.FunctionAnnotation
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation

data class MyFunction(
    val name: String,
    val returnType: MyType,
    val isSuspend: Boolean = false,
    val params: List<MyParam>,
    val annotations: List<FunctionAnnotation> = emptyList(),
    val httpMethodAnnotation: HttpMethodAnnotation
)