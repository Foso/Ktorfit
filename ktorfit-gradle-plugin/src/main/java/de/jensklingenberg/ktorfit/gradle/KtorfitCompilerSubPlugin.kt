package de.jensklingenberg.ktorfit.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

internal class KtorfitCompilerSubPlugin : KotlinCompilerPluginSupportPlugin {

    companion object {
        const val GROUP_NAME = "de.jensklingenberg.ktorfit"
        const val ARTIFACT_NAME = "compiler-plugin"
        const val COMPILER_PLUGIN_ID = "ktorfitPlugin"
        const val KTORFIT_VERSION = "2.0.0"
        const val SNAPSHOT = ""
    }

    private lateinit var myproject: Project
    private var gradleExtension: KtorfitGradleConfiguration = KtorfitGradleConfiguration()
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        gradleExtension = kotlinCompilation.target.project.extensions.findByType(KtorfitGradleConfiguration::class.java)
            ?: KtorfitGradleConfiguration()

        return kotlinCompilation.target.project.provider {
            listOf(
                SubpluginOption("enabled", "true"),
                SubpluginOption("logging", gradleExtension.logging.toString())
            )
        }
    }

    override fun apply(target: Project) {
        myproject = target
    }

    override fun getCompilerPluginId(): String = COMPILER_PLUGIN_ID

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = GROUP_NAME,
            artifactId = ARTIFACT_NAME,
            version = "${KTORFIT_VERSION}-${myproject.kotlinExtension.compilerVersion.get()}${SNAPSHOT}" // remember to bump this version before any release!
        )
    }
}
