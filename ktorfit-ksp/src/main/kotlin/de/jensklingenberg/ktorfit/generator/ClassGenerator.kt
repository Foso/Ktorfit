package de.jensklingenberg.ktorfit.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import de.jensklingenberg.ktorfit.KtorfitOptions
import de.jensklingenberg.ktorfit.model.ClassData
import de.jensklingenberg.ktorfit.model.getImplClassFileSource
import java.io.OutputStreamWriter


/**
 * Generate the Impl class for every interface used for Ktorfit
 */
fun generateImplClass(
    classDataList: List<ClassData>,
    codeGenerator: CodeGenerator,
    resolver: Resolver,
    ktorfitOptions: KtorfitOptions
) {
    classDataList.forEach { classData ->
        val fileSource = classData.getImplClassFileSource(resolver, ktorfitOptions)

        val packageName = classData.packageName
        val className = classData.name
        val fileName = "_${className}Impl"

        codeGenerator.createNewFile(dependencies = Dependencies(false, classData.ksFile), packageName, fileName, "kt")
            .use { output ->
                OutputStreamWriter(output).use { writer ->
                    writer.write(fileSource)
                }
            }
    }
}

