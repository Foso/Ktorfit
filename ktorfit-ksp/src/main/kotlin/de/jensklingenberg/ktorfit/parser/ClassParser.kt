package de.jensklingenberg.ktorfit.parser

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import de.jensklingenberg.ktorfit.ktorfitError
import de.jensklingenberg.ktorfit.model.MyClass
import de.jensklingenberg.ktorfit.model.MyFunction
import java.io.File


/**
 *  //TODO: Find better way to get imports
 */
private fun getImports(ksClassDeclaration: KSClassDeclaration): List<String> {
    val importList =
        File(ksClassDeclaration.containingFile!!.filePath)
            .readLines()
            .filter { it.trimStart().startsWith("import") }
            .toMutableList()

    importList.addIfAbsent("import de.jensklingenberg.ktorfit.Ktorfit")
    importList.addIfAbsent("import de.jensklingenberg.ktorfit.internal.KtorfitClient")
    importList.addIfAbsent("import de.jensklingenberg.ktorfit.internal.RequestData")
    importList.addIfAbsent("import de.jensklingenberg.ktorfit.internal.QueryData")
    importList.addIfAbsent("import de.jensklingenberg.ktorfit.internal.QueryType")
    importList.addIfAbsent("import de.jensklingenberg.ktorfit.internal.HeaderData")
    importList.addIfAbsent("import de.jensklingenberg.ktorfit.internal.FieldData")
    importList.addIfAbsent("import de.jensklingenberg.ktorfit.internal.FieldType")

    return importList
}

private fun MutableList<String>.addIfAbsent(e2: String) {
    if (this.none { it.contains(e2) }) {
        this.add(e2)
    }
}

fun toMyClass(ksClassDeclaration: KSClassDeclaration, logger: KSPLogger): MyClass {

    val myFunctions: List<MyFunction> = getMyFunctionsList(ksClassDeclaration.getDeclaredFunctions().toList(), logger)

    val imports = getImports(ksClassDeclaration)
    val packageName = ksClassDeclaration.packageName.asString()
    val className = ksClassDeclaration.simpleName.asString()

    val supertypes =
        ksClassDeclaration.superTypes.toList().mapNotNull { it.resolve().declaration.qualifiedName?.asString() }
    val properties = ksClassDeclaration.getAllProperties().toList()

    if (packageName.isEmpty()) {
        logger.ktorfitError("Interface needs to have a package", ksClassDeclaration)
    }
    return MyClass(className, packageName, myFunctions, imports, supertypes, properties)
}
