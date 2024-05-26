package de.jensklingenberg.ktorfit.generator

import com.google.devtools.ksp.KspExperimental
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
@OptIn(KspExperimental::class)
fun generateImplClass(
    classDataList: List<ClassData>,
    codeGenerator: CodeGenerator,
    resolver: Resolver,
    ktorfitOptions: KtorfitOptions
) {
    classDataList.forEach { classData ->
        with(classData) {
            val fileSource = classData.getImplClassFileSource(resolver, ktorfitOptions)

            val fileName = "_${name}Impl"
            val commonMainModuleName = "commonMain"
            val moduleName = resolver.getModuleName().getShortName()
            if (moduleName.contains(commonMainModuleName)) {
                if (!ksFile.filePath.contains(commonMainModuleName)) {
                    return@forEach
                }
            } else {
                if (ksFile.filePath.contains(commonMainModuleName)) {
                    return@forEach
                }
            }

            codeGenerator.createNewFile(dependencies = Dependencies(false, ksFile), packageName, fileName, "kt")
                .use { output ->
                    OutputStreamWriter(output).use { writer ->
                        writer.write(fileSource)
                    }
                }
        }

    }
}

