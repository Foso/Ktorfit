package de.jensklingenberg.ktorfit.model

import com.squareup.kotlinpoet.ClassName

data class KtorfitClass(val name: String, val packageName: String, val objectName: String)

val ktorfitClientClass = KtorfitClass("KtorfitClient", "de.jensklingenberg.ktorfit.internal", "ktorfitClient")
val clientClass = KtorfitClass("Client", "de.jensklingenberg.ktorfit.internal", "ktorfitClient")
val ktorfitClass = KtorfitClass("Ktorfit", "de.jensklingenberg.ktorfit", "")
val typeDataClass = KtorfitClass("TypeData", "de.jensklingenberg.ktorfit.internal", "__typeData")
val ktorfitServiceClassName = ClassName("de.jensklingenberg.ktorfit.internal", "KtorfitService")

