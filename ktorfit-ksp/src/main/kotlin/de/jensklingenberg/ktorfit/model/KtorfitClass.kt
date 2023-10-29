package de.jensklingenberg.ktorfit.model

import com.squareup.kotlinpoet.ClassName

data class KtorfitClass(val name: String, val packageName: String, val objectName: String)

val ktorfitClass = KtorfitClass("Ktorfit", "de.jensklingenberg.ktorfit", "_ktorfit")
val typeDataClass = KtorfitClass("TypeData", "de.jensklingenberg.ktorfit.internal", "_typeData")
val extDataClass = KtorfitClass("HttpRequestBuilder.() -> Unit", "", "_ext")
val formParameters = KtorfitClass("", "", "__formParameters")
val internalApi = ClassName("de.jensklingenberg.ktorfit.internal", "InternalKtorfitApi")
val internalKtorfitClientType = ClassName("de.jensklingenberg.ktorfit.internal", "KtorfitConverterHelper")

fun KtorfitClass.toClassName() = ClassName(packageName, name)