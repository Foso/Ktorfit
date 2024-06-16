package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.symbol.KSType

data class ReturnTypeData(
    val name: String,
    val parameterType: KSType,
)
