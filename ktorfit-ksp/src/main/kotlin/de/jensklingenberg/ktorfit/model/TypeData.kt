package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.getClassDeclarationByName
import de.jensklingenberg.ktorfit.utils.surroundIfNotEmpty


data class TypeData(val qualifiedName: String, val typeArgs: List<TypeData> = emptyList(), val returnTypeInfo: String) {
    override fun toString(): String {
        val typeArgumentsText = typeArgs.joinToString { it.toString() }.surroundIfNotEmpty(",listOf(", ")")
        return """TypeData("$qualifiedName"$typeArgumentsText, typeInfo = typeInfo<$returnTypeInfo>())"""
    }

    companion object {

        //https://kotlinlang.org/docs/packages.html
        private fun defaultImports() = listOf(
            "kotlin.*",
            "kotlin.annotation.*",
            "kotlin.collections.*",
            "kotlin.comparisons.*",
            "kotlin.io.*",
            "kotlin.ranges.*",
            "kotlin.sequences.*",
            "kotlin.text.*"
        )

        //TODO: Add test
        fun getMyType(
            text: String,
            imports: List<String>,
            packageName: String,
            resolver: com.google.devtools.ksp.processing.Resolver
        ): TypeData {
            val classImports = imports + defaultImports()
            var className = text.substringBefore("<", "")
            if (className.isEmpty()) {
                className = text.substringBefore(",", "")
            }
            if (className.isEmpty()) {
                className = text
            }
            val type = (text.removePrefix(className)).substringAfter("<").substringBeforeLast(">")
            val argumentsTypes = mutableListOf<TypeData>()

            val hasTypeArgs = type.contains("<")
            val isTypeArgument = type.contains(",")

            if (hasTypeArgs) {
                argumentsTypes.add(getMyType(type, classImports, packageName, resolver))
            } else if (isTypeArgument) {
                type.split(",").forEach {
                    argumentsTypes.add(getMyType(it, classImports, packageName, resolver))
                }
            } else if (type.isNotEmpty()) {
                argumentsTypes.add(getMyType(type, classImports, packageName, resolver))
            }


            //Look in package
            resolver.getClassDeclarationByName("$packageName.$className")?.qualifiedName?.asString()?.let {
                className = it
            }

            //Wildcards
            val isWildCard = className == "*"
            if (!isWildCard) {
                classImports.forEach {
                    if (it.substringAfterLast(".") == className) {
                        className = it
                    }

                    val packageName = it.substringBeforeLast(".")
                    val found2 =
                        resolver.getClassDeclarationByName("$packageName.$className")?.qualifiedName?.asString()
                    found2?.let {
                        className = it
                    }
                }
            }
            val nullable = if (text.endsWith("?") && !className.endsWith("?")) {
                "?"
            } else {
                ""
            }

            return TypeData(className + nullable, argumentsTypes, text)
        }
    }
}

