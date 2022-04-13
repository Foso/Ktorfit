package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.symbol.KSPropertyDeclaration

/**
 * @param superClasses List of qualifiedNames of interface that a Ktorfit interface extends
 */
data class MyClass(
    val name: String,
    val packageName: String,
    val functions: List<MyFunction>,
    val imports: List<String>,
    val superClasses: List<String> = emptyList(),
    val properties: List<KSPropertyDeclaration> = emptyList()
)