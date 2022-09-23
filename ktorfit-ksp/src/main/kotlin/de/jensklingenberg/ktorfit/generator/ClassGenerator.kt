package de.jensklingenberg.ktorfit.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import de.jensklingenberg.ktorfit.model.ClassData
import de.jensklingenberg.ktorfit.model.getImplClassFileSource
import java.io.OutputStreamWriter


/**
 * Generate the Impl class for every interface used for Ktorfit
 */
fun generateImplClass(classDataList: List<ClassData>, codeGenerator: CodeGenerator) {
    classDataList.forEach { classData ->
        val fileSource = classData.getImplClassFileSource()

        val packageName = classData.packageName
        val className = classData.name
        val fileName = "_${className}Impl"

        codeGenerator.createNewFile(Dependencies.ALL_FILES, packageName, fileName , "kt").use { output ->
            OutputStreamWriter(output).use { writer ->
                writer.write(fileSource)
            }
        }
    }
}

