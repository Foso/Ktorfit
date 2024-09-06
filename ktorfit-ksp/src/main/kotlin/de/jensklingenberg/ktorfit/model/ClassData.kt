package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import de.jensklingenberg.ktorfit.model.annotations.FormUrlEncoded
import de.jensklingenberg.ktorfit.model.annotations.Multipart
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Field

/**
 * @param name of the interface that contains annotations
 * @param superClasses List of qualifiedNames of interface that a Ktorfit interface extends
 */
data class ClassData(
    val name: String,
    val packageName: String,
    val functions: List<FunctionData>,
    val imports: List<String>,
    val superClasses: List<KSTypeReference> = emptyList(),
    val properties: List<KSPropertyDeclaration> = emptyList(),
    val modifiers: List<KModifier> = emptyList(),
    val ksFile: KSFile,
)

/**
 * Convert a [KSClassDeclaration] to [ClassData]
 * @param logger used to log errors
 * @return the transformed classdata
 */
fun KSClassDeclaration.toClassData(logger: KSPLogger): ClassData {
    val ksClassDeclaration = this
    val imports =
        mutableListOf(
            "io.ktor.util.reflect.typeInfo",
            "io.ktor.client.request.HttpRequestBuilder",
            "io.ktor.client.request.setBody",
            "io.ktor.client.request.headers",
            "io.ktor.client.request.parameter",
            "io.ktor.http.URLBuilder",
            "io.ktor.http.HttpMethod",
            "io.ktor.http.takeFrom",
            "io.ktor.http.decodeURLQueryComponent",
            "io.ktor.http.encodeURLPath",
            typeDataClass.packageName + "." + typeDataClass.name,
        )

    val packageName = ksClassDeclaration.packageName.asString()
    val className = ksClassDeclaration.simpleName.asString()

    checkClassForErrors(this, logger)

    val functionDataList: List<FunctionData> =
        ksClassDeclaration.getDeclaredFunctions().toList().map { funcDeclaration ->
            return@map funcDeclaration.toFunctionData(logger)
        }

    if (functionDataList.any { it ->
            it.annotations.any { it is FormUrlEncoded || it is Multipart } ||
                it.parameterDataList.any { param -> param.hasAnnotation<Field>() || param.hasAnnotation<ParameterAnnotation.Part>() }
        }
    ) {
        imports.add("io.ktor.client.request.forms.FormDataContent")
        imports.add("io.ktor.client.request.forms.MultiPartFormDataContent")
        imports.add("io.ktor.client.request.forms.formData")
        imports.add("io.ktor.http.Parameters")
    }

    if (functionDataList.any { it.parameterDataList.any { param -> param.hasAnnotation<ParameterAnnotation.RequestType>() } }) {
        imports.add("kotlin.reflect.cast")
    }

    val filteredSupertypes =
        ksClassDeclaration.superTypes.toList().filterNot {
            /** In KSP Any is a supertype of an interface */
            it.toTypeName() == ANY
        }
    val properties = ksClassDeclaration.getAllProperties().toList()

    return ClassData(
        name = className,
        packageName = packageName,
        functions = functionDataList,
        imports = imports,
        superClasses = filteredSupertypes,
        properties = properties,
        modifiers = ksClassDeclaration.modifiers.mapNotNull { it.toKModifier() },
        ksFile = ksClassDeclaration.getKsFile(),
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
        return
    }

    if (ksClassDeclaration.packageName.asString().isEmpty()) {
        logger.error(KtorfitError.INTERFACE_NEEDS_TO_HAVE_A_PACKAGE, ksClassDeclaration)
        return
    }
}

private fun KSClassDeclaration.getKsFile(): KSFile = this.containingFile ?: throw Error("Containing File for ${this.simpleName} was null")
