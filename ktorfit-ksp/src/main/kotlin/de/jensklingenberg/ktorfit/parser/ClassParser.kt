package de.jensklingenberg.ktorfit.parser

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ksp.toKModifier
import de.jensklingenberg.ktorfit.generator.clientClass
import de.jensklingenberg.ktorfit.generator.ktorfitClass
import de.jensklingenberg.ktorfit.ktorfitError
import de.jensklingenberg.ktorfit.model.ClassData
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.INTERFACE_NEEDS_TO_HAVE_A_PACKAGE
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.INTERNAL_INTERFACES_ARE_NOT_SUPPORTED
import de.jensklingenberg.ktorfit.resolveTypeName
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

    importList.addIfAbsent(ktorfitClass.packageName+"."+ ktorfitClass.name)
    importList.addIfAbsent(clientClass.packageName+"."+ clientClass.name)
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.RequestData")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.QueryData")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.QueryType")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.HeaderData")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.FieldData")
    importList.addIfAbsent("de.jensklingenberg.ktorfit.internal.FieldType")

    return importList.map { it.removePrefix("import ") }
}

private fun MutableList<String>.addIfAbsent(text: String) {
    if (this.none { it.contains(text) }) {
        this.add(text)
    }
}

fun toClassData(ksClassDeclaration: KSClassDeclaration, logger: KSPLogger): ClassData {

    val functionDataList: List<FunctionData> =
        getFunctionDataList(ksClassDeclaration.getDeclaredFunctions().toList(), logger)

    val imports = getImports(ksClassDeclaration)
    val packageName = ksClassDeclaration.packageName.asString()
    val className = ksClassDeclaration.simpleName.asString()

    val supertypes =
        ksClassDeclaration.superTypes.toList().filterNot {
            /** In KSP Any is a supertype of an interface */
            it.resolve().resolveTypeName() == "Any"
        }.mapNotNull { it.resolve().declaration.qualifiedName?.asString() }
    val properties = ksClassDeclaration.getAllProperties().toList()

    if (packageName.isEmpty()) {
        logger.ktorfitError(INTERFACE_NEEDS_TO_HAVE_A_PACKAGE, ksClassDeclaration)
    }

    if (ksClassDeclaration.modifiers.contains(Modifier.INTERNAL)) {
        logger.ktorfitError(INTERNAL_INTERFACES_ARE_NOT_SUPPORTED, ksClassDeclaration)
    }

    return ClassData(
        className,
        packageName,
        functionDataList,
        imports,
        supertypes,
        properties,
        modifiers = ksClassDeclaration.modifiers.mapNotNull { it.toKModifier() })
}
