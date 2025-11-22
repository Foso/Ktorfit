package de.jensklingenberg.ktorfit.model

import com.squareup.kotlinpoet.ClassName

data class KtorfitClass(
    val name: String,
    val packageName: String,
    val objectName: String
)

val ktorfitClass = KtorfitClass("Ktorfit", "de.jensklingenberg.ktorfit", "_ktorfit")
val providerClass = KtorfitClass("ClassProvider", "de.jensklingenberg.ktorfit.internal", "EMPTY")
val typeDataClass = KtorfitClass("TypeData", "de.jensklingenberg.ktorfit.converter", "_typeData")
val extDataClass = KtorfitClass("HttpRequestBuilder.() -> Unit", "", "_ext")
val formParameters = KtorfitClass("", "", "__formParameters")
val formData = KtorfitClass("", "", "__formData")
val converterHelper = KtorfitClass("KtorfitConverterHelper", "de.jensklingenberg.ktorfit.internal", "__helper")
val internalApi = ClassName("de.jensklingenberg.ktorfit.internal", "InternalKtorfitApi")
val annotationsAttributeKey = KtorfitClass("annotationsAttributeKey", "de.jensklingenberg.ktorfit", "AttributeKey(\"__ktorfit_attribute_annotations\")")
val httpClient = KtorfitClass("HttpClient", "io.ktor.client", "__httpClient")
val baseUrl = KtorfitClass("String", "kotlin", "__baseUrl")
fun KtorfitClass.toClassName() = ClassName(packageName, name)
