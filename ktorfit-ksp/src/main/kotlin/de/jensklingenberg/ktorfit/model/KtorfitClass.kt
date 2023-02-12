package de.jensklingenberg.ktorfit.model

import com.squareup.kotlinpoet.ClassName

data class KtorfitClass(val name: String, val packageName: String, val objectName: String)

val ktorfitClientClass = KtorfitClass("KtorfitClient", "de.jensklingenberg.ktorfit.internal", "client")
val ktorfitClass = KtorfitClass("Ktorfit", "de.jensklingenberg.ktorfit", "")
val requestDataClass = KtorfitClass("RequestData", "de.jensklingenberg.ktorfit.internal", "requestData")
val pathDataClass = KtorfitClass("PathData", "de.jensklingenberg.ktorfit.internal", "")
val ktorfitServiceClassName = ClassName("de.jensklingenberg.ktorfit.internal", "KtorfitService")

fun KtorfitClass.toClassName() = ClassName(this.packageName, this.name)
