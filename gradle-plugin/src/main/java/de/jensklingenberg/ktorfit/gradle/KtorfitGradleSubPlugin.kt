package de.jensklingenberg.ktorfit.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

open class KtorfitGradleConfiguration {
    var enabled: Boolean = true
    var version: String = "1.0.0-beta17" // remember to bump this version before any release!
    var logging: Boolean = false
}


class KtorfitGradleSubPlugin : KotlinCompilerPluginSupportPlugin {

    companion object {
        const val SERIALIZATION_GROUP_NAME = "de.jensklingenberg.ktorfit"
        const val ARTIFACT_NAME = "compiler-plugin"
        const val COMPILER_PLUGIN_ID = "ktorfitPlugin"
        const val GRADLE_TASKNAME = "ktorfit"
    }

    private lateinit var myproject: Project
    private var gradleExtension: KtorfitGradleConfiguration = KtorfitGradleConfiguration()
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        gradleExtension = kotlinCompilation.target.project.extensions.findByType(KtorfitGradleConfiguration::class.java)
            ?: KtorfitGradleConfiguration()

        return kotlinCompilation.target.project.provider {
            val options = mutableListOf(
                SubpluginOption("enabled", gradleExtension.enabled.toString()),
                SubpluginOption("logging", gradleExtension.logging.toString())
            )
            options
        }
    }

    private fun Project.getKtorfitConfig() =
        this.extensions.findByType(KtorfitGradleConfiguration::class.java) ?: KtorfitGradleConfiguration()

    override fun apply(target: Project) {
        target.extensions.create(GRADLE_TASKNAME, KtorfitGradleConfiguration::class.java)
        myproject = target
        super.apply(target)
    }

    override fun getCompilerPluginId(): String = COMPILER_PLUGIN_ID

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = SERIALIZATION_GROUP_NAME,
            artifactId = ARTIFACT_NAME,
            version = myproject.getKtorfitConfig().version
        )
    }
}
