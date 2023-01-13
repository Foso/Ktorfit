package de.jensklingenberg

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.config.KotlinSourceRoot
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.createSourceFilesFromSourceRoots
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.extensions.AnalysisHandlerExtension
import java.io.File
import java.nio.file.Files

class MyAnalysisHandlerExtension(
    val messageCollector: MessageCollector,
    val output: File?,
    val configuration: CompilerConfiguration
) : AnalysisHandlerExtension {

   companion object{
       var needRetry = false
   }



    override fun doAnalysis(
        project: Project,
        module: ModuleDescriptor,
        projectContext: ProjectContext,
        files: Collection<KtFile>,
        bindingTrace: BindingTrace,
        componentProvider: ComponentProvider
    ): AnalysisResult? {

        files as ArrayList

        needRetry = files.none { it.virtualFilePath.contains("_Json") }

        for (i in files.indices) {
            val oldFile = files[i]
            messageCollector.report(CompilerMessageSeverity.INFO, oldFile.name)

            // Replace text and create a new KtFile
            val newFileText: String? =  TreeVis(oldFile, messageCollector).buildOutput()


            // Create new file base on transformed text, and replace the old file with the new one
            if (newFileText != null) {

                val tt = newFileText

                //files[i] = createNewKtFile(oldFile.name, tt, output!!.path, oldFile.manager)

            }
            messageCollector.report(
                CompilerMessageSeverity.STRONG_WARNING,
                "${oldFile.virtualFilePath} transformed to ${files[i].virtualFilePath}"
            )

        }

        val source = """
            package com.example.api

            import de.jensklingenberg.ktorfit.internal.KtorfitClient
            import de.jensklingenberg.ktorfit.Hidden

            class _JsonPlaceHolderApiImpl : Hidden {
                override fun setClient(client: KtorfitClient) {

                }

            }
        """.trimIndent()

        val secTemp = Files.createTempDirectory("mytemp")
        val ff = File(secTemp.toAbsolutePath().toString() + "/_Json.kt").also {
             it.writeText(source)
         }
       // val sourceFiles = createSourceFilesFromSourceRoots(configuration, project, listOf(KotlinSourceRoot(secTemp.toAbsolutePath().toString() + "/temp.kt",true)))

       // files.addAll(sourceFiles)

        if(needRetry){
            messageCollector.report(
                CompilerMessageSeverity.STRONG_WARNING,
                "IS RETRY============================="
            )
            needRetry=false
            return AnalysisResult.RetryWithAdditionalRoots(
                bindingTrace.bindingContext, module, emptyList(), listOf(ff), addToEnvironment = true
            )
        }

        return super.doAnalysis(project, module, projectContext, files, bindingTrace, componentProvider)
    }
}