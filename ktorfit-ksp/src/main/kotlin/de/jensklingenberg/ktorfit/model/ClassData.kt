package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import de.jensklingenberg.ktorfit.utils.getKsFile

/**
 * @param name of the interface that contains annotations
 * @param superClasses List of qualifiedNames of interface that a Ktorfit interface extends
 */
data class ClassData(
    val name: String,
    val packageName: String,
    val functions: List<FunctionData>,
    val imports: Set<String>,
    val superClasses: List<KSTypeReference> = emptyList(),
    val properties: List<KSPropertyDeclaration> = emptyList(),
    val modifiers: List<KModifier> = emptyList(),
    val ksFile: KSFile,
    val annotations: List<KSAnnotation>,
) {
    val implName = "_${name}Impl"
    val providerName = "_${name}Provider"
}

fun ClassData.getConverters(): List<KSClassDeclaration> {
    val anno = this.annotations.firstOrNull() {
        it.shortName.asString() == "TypeConverters"
    }
    // Vararg KClass<*> becomes a List<KSType>
    val ksTypes = anno?.arguments?.firstOrNull() { it.name?.asString() == "value" }?.value as? List<KSType>
    val converterClassDecls = ksTypes?.mapNotNull { it.declaration as? KSClassDeclaration }.orEmpty()
    return converterClassDecls
}

/**
 * Convert a [KSClassDeclaration] to [ClassData]
 * @param logger used to log errors
 * @return the transformed classdata
 */
fun KSClassDeclaration.toClassData(logger: KSPLogger, ktorfitLib: Boolean): ClassData {
    val ksClassDeclaration = this
    val imports =
        mutableSetOf(
            "io.ktor.util.reflect.typeInfo",
            "io.ktor.client.request.HttpRequestBuilder",
            "io.ktor.client.request.parameter",
            "io.ktor.http.URLBuilder",
            "io.ktor.http.takeFrom",
            "io.ktor.http.decodeURLQueryComponent",
            "io.ktor.client.request.request",
            "io.ktor.client.call.body",
            annotationsAttributeKey.packageName + "." + annotationsAttributeKey.name,
           // typeDataClass.packageName + "." + typeDataClass.name,
        )




    val packageName = ksClassDeclaration.packageName.asString()
    val className = ksClassDeclaration.simpleName.asString()

    checkClassForErrors(this, logger)

    val functionDataList: List<FunctionData> =
        ksClassDeclaration
            .getDeclaredFunctions()
            .toList()
            .map { funcDeclaration ->
                return@map funcDeclaration.toFunctionData(logger, addImport = { imports.add(it) })
            }

    val filteredSupertypes =
        ksClassDeclaration.superTypes.toList().filterNot {
            /** In KSP Any is a supertype of an interface */
            it.toTypeName() == ANY
        }
    val properties = ksClassDeclaration.getDeclaredProperties().toList()

    return ClassData(
        name = className,
        packageName = packageName,
        functions = functionDataList,
        imports = imports,
        superClasses = filteredSupertypes,
        properties = properties,
        modifiers = ksClassDeclaration.modifiers.mapNotNull { it.toKModifier() },
        ksFile = ksClassDeclaration.getKsFile(),
        annotations = ksClassDeclaration.annotations.toList()
    )
}

private fun checkClassForErrors(
    ksClassDeclaration: KSClassDeclaration,
    logger: KSPLogger,
) {
    val isJavaClass = ksClassDeclaration.origin.name == "JAVA"
    if (isJavaClass) {
        logger.error(KtorfitError.JAVA_INTERFACES_ARE_NOT_SUPPORTED, ksClassDeclaration)
        return
    }

    val isInterface = ksClassDeclaration.classKind == ClassKind.INTERFACE
    if (!isInterface) {
        logger.error(KtorfitError.API_DECLARATIONS_MUST_BE_INTERFACES, ksClassDeclaration)
        return
    }

    val hasTypeParameters = ksClassDeclaration.typeParameters.isNotEmpty()
    if (hasTypeParameters) {
        logger.error(
            KtorfitError.TYPE_PARAMETERS_ARE_UNSUPPORTED_ON + " ${ksClassDeclaration.simpleName.asString()}",
            ksClassDeclaration,
        )
        return
    }

    if (ksClassDeclaration.packageName.asString().isEmpty()) {
        logger.error(KtorfitError.INTERFACE_NEEDS_TO_HAVE_A_PACKAGE, ksClassDeclaration)
        return
    }
}
