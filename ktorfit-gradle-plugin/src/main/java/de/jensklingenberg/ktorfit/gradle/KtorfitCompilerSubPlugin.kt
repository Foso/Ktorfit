package de.jensklingenberg.ktorfit.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

internal class KtorfitCompilerSubPlugin : KotlinCompilerPluginSupportPlugin {

    companion object {
        const val SERIALIZATION_GROUP_NAME = "de.jensklingenberg.ktorfit"
        const val ARTIFACT_NAME = "compiler-plugin"
        const val COMPILER_PLUGIN_ID = "ktorfitPlugin"
    }

    private lateinit var myproject: Project
    private var gradleExtension: KtorfitGradleConfiguration = KtorfitGradleConfiguration()
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        gradleExtension = kotlinCompilation.target.project.extensions.findByType(KtorfitGradleConfiguration::class.java)
            ?: KtorfitGradleConfiguration()

        return kotlinCompilation.target.project.provider {
            listOf(
                SubpluginOption("enabled", gradleExtension.enabled.toString()),
                SubpluginOption("logging", gradleExtension.logging.toString())
            )
        }
    }

    private fun Project.getKtorfitConfig() =
        this.extensions.findByType(KtorfitGradleConfiguration::class.java) ?: KtorfitGradleConfiguration()

    override fun apply(target: Project) {
        myproject = target
    }

    override fun getCompilerPluginId(): String = COMPILER_PLUGIN_ID

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        myproject.getKtorfitConfig()
        return SubpluginArtifact(
            groupId = SERIALIZATION_GROUP_NAME,
            artifactId = ARTIFACT_NAME,
            version = "2.0.0-SNAPSHOT" // remember to bump this version before any release!
        )
    }
}
