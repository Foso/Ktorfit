package de.jensklingenberg.ktorfit.gradle

import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.ARTIFACT_NAME
import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.COMPILER_PLUGIN_ID
import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.GRADLE_TASKNAME
import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.GROUP_NAME
import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.KTORFIT_COMPILER_PLUGIN_VERSION
import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.MIN_KOTLIN_VERSION
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

internal class KtorfitCompilerSubPlugin : KotlinCompilerPluginSupportPlugin {
    private lateinit var myProject: Project

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        return kotlinCompilation.target.project.provider {
            listOf(
                SubpluginOption("enabled", "true"),
                SubpluginOption("logging", "false"),
            )
        }
    }

    override fun apply(target: Project) {
        myProject = target
    }

    override fun getCompilerPluginId(): String = COMPILER_PLUGIN_ID

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        val kotlinVersion =
            myProject.ktorfitExtension(
                GRADLE_TASKNAME
            ).compilerPluginVersion.get()

        return kotlinVersion != "-"
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalBuildToolsApi::class)
    override fun getPluginArtifact(): SubpluginArtifact {
        checkKotlinVersion(myProject.kotlinExtension.compilerVersion.get())
        val compilerVersion =
            myProject.ktorfitExtension(
                GRADLE_TASKNAME
            ).compilerPluginVersion.getOrElse(KTORFIT_COMPILER_PLUGIN_VERSION)

        return SubpluginArtifact(
            groupId = GROUP_NAME,
            artifactId = ARTIFACT_NAME,
            version = compilerVersion
        )
    }

    private fun checkKotlinVersion(compilerVersion: String) {
        if (compilerVersion.split(".")[0] < MIN_KOTLIN_VERSION.split(".")[0]) {
            error("Ktorfit: Kotlin version $compilerVersion is not supported. You need at least version $MIN_KOTLIN_VERSION")
        }
    }
}
