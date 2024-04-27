package de.jensklingenberg.ktorfit.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class KtorfitGradlePlugin : Plugin<Project> {
    companion object {
        const val GRADLE_TASKNAME = "ktorfit"
    }

    override fun apply(project: Project) {
        project.pluginManager.apply(KtorfitCompilerSubPlugin::class.java)
    }


}