package de.jensklingenberg.ktorfit.model

import com.squareup.kotlinpoet.ClassName

data class KtorfitClass(val name: String, val packageName: String, val objectName: String)

val ktorfitClientClass = KtorfitClass("KtorfitClient", "de.jensklingenberg.ktorfit.internal", "ktorfitClient")
val clientClass = KtorfitClass("Client", "de.jensklingenberg.ktorfit.internal", "ktorfitClient")

val ktorfitClass = KtorfitClass("Ktorfit", "de.jensklingenberg.ktorfit", "")
val requestDataClass = KtorfitClass("RequestData", "de.jensklingenberg.ktorfit.internal", "_requestData")
val ktorfitServiceClassName = ClassName("de.jensklingenberg.ktorfit.internal", "KtorfitService")

fun KtorfitClass.toClassName() = ClassName(this.packageName, this.name)
