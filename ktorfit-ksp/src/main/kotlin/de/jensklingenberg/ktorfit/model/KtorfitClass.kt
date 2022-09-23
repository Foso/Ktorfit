package de.jensklingenberg.ktorfit.model

data class KtorfitClass(val name: String, val packageName: String, val objectName: String)

val clientClass = KtorfitClass("KtorfitClient", "de.jensklingenberg.ktorfit.internal", "client")
val ktorfitClass = KtorfitClass("Ktorfit", "de.jensklingenberg.ktorfit", "")
val requestDataClass = KtorfitClass("RequestData", "de.jensklingenberg.ktorfit.internal", "requestData")
val ktorfitExtClass = KtorfitClass("KtorfitExt", "de.jensklingenberg.ktorfit", "")
val pathDataClass = KtorfitClass("PathData", "de.jensklingenberg.ktorfit.internal", "")
