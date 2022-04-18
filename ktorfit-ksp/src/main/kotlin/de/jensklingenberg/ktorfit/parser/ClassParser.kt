package de.jensklingenberg.ktorfit.parser

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import de.jensklingenberg.ktorfit.ktorfitError
import de.jensklingenberg.ktorfit.model.ClassData
import de.jensklingenberg.ktorfit.model.FunctionData
import java.io.File


/**
 *  TODO: Find better way to get imports
 */
private fun getImports(ksClassDeclaration: KSClassDeclaration): List<String> {
    val importList =
        File(ksClassDeclaration.containingFile!!.filePath)
            .readLines()
            .filter { it.trimStart().startsWith("import") }
            .toMutableList()

    importList.addIfAbsent("de.jensklingenberg.ktorfit.Ktorfit")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.KtorfitClient")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.RequestData")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.QueryData")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.QueryType")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.HeaderData")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.FieldData")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.FieldType")

    return importList.map { it.removePrefix("import ") }
}

private fun MutableList<String>.addIfAbsent(e2: String) {
    if (this.none { it.contains(e2) }) {
        this.add(e2)
    }
}

fun toClassData(ksClassDeclaration: KSClassDeclaration, logger: KSPLogger): ClassData {

    val functionDataList: List<FunctionData> = getFunctionDataList(ksClassDeclaration.getDeclaredFunctions().toList(), logger)

    val imports = getImports(ksClassDeclaration)
    val packageName = ksClassDeclaration.packageName.asString()
    val className = ksClassDeclaration.simpleName.asString()

    val supertypes =
        ksClassDeclaration.superTypes.toList().mapNotNull { it.resolve().declaration.qualifiedName?.asString() }
    val properties = ksClassDeclaration.getAllProperties().toList()

    if (packageName.isEmpty()) {
        logger.ktorfitError("Interface needs to have a package", ksClassDeclaration)
    }
    return ClassData(className, packageName, functionDataList, imports, supertypes, properties)
}
