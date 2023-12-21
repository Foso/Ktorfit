package de.jensklingenberg.ktorfit.model

import com.squareup.kotlinpoet.ClassName

data class KtorfitClass(val name: String, val packageName: String, val objectName: String)

val ktorfitClass = KtorfitClass("Ktorfit", "de.jensklingenberg.ktorfit", "_ktorfit")
val ktorfitInterface = KtorfitClass("KtorfitInterface", "de.jensklingenberg.ktorfit.internal", "EMPTY")
val typeDataClass = KtorfitClass("TypeData", "de.jensklingenberg.ktorfit.internal", "_typeData")
val extDataClass = KtorfitClass("HttpRequestBuilder.() -> Unit", "", "_ext")
val formParameters = KtorfitClass("", "", "__formParameters")
val converterHelper = KtorfitClass("KtorfitConverterHelper","de.jensklingenberg.ktorfit.internal", "_converter")
val internalApi = ClassName("de.jensklingenberg.ktorfit.internal", "InternalKtorfitApi")

fun KtorfitClass.toClassName() = ClassName(packageName, name)