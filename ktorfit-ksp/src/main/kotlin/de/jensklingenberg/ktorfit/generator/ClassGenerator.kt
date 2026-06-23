package de.jensklingenberg.ktorfit.generator

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import de.jensklingenberg.ktorfit.KtorfitOptions
import de.jensklingenberg.ktorfit.model.ClassData
import de.jensklingenberg.ktorfit.poetspec.createFileSpec
import de.jensklingenberg.ktorfit.poetspec.getImplClassSpec
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
            val implClassSpec = classData.getImplClassSpec(resolver, ktorfitOptions)

            val fileSource =
                createFileSpec(
                    classData,
                    classData.implName,
                    implClassSpec
                ).toString()

            codeGenerator
                .createNewFile(
                    dependencies = Dependencies(false, ksFile),
                    packageName,
                    classData.implName,
                    "kt"
                )
                .use { output ->
                    OutputStreamWriter(output).use { writer ->
                        writer.write(fileSource)
                    }
                }
        }
    }
}
