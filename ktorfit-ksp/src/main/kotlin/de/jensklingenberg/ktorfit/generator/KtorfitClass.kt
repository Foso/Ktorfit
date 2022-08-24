package de.jensklingenberg.ktorfit.generator

data class KtorfitClass(val name: String, val packageName: String, val objectName : String)
val clientClass = KtorfitClass("KtorfitClient","de.jensklingenberg.ktorfit.internal","client")
val ktorfitClass = KtorfitClass("Ktorfit","de.jensklingenberg.ktorfit","")