package de.jensklingenberg.ktorfit

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

open class KtorfitGradleConfiguration {
    var enabled: Boolean = true
    var version: String = "1.0.0-beta16"
}


class KtorfitGradleSubPlugin : KotlinCompilerPluginSupportPlugin {

    companion object {
        const val SERIALIZATION_GROUP_NAME = "de.jensklingenberg.ktorfit"
        const val ARTIFACT_NAME = "compiler-plugin"
        const val NATIVE_ARTIFACT_NAME = "$ARTIFACT_NAME-native"
        const val VERSION_NUMBER = "1.0.0-beta16"
    }

    private var gradleExtension : KtorfitGradleConfiguration = KtorfitGradleConfiguration()
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        gradleExtension = kotlinCompilation.target.project.extensions.findByType(KtorfitGradleConfiguration::class.java) ?: KtorfitGradleConfiguration()

        return kotlinCompilation.target.project.provider {
            val options = mutableListOf(SubpluginOption("enabled", gradleExtension.enabled.toString()))
            options
        }
    }

    override fun apply(target: Project) {
        target.extensions.create(
            "helloWorld",
            KtorfitGradleConfiguration::class.java
        )
        super.apply(target)
    }

    override fun getCompilerPluginId(): String = "helloWorldPlugin"

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = SERIALIZATION_GROUP_NAME,
        artifactId = ARTIFACT_NAME,
        version = gradleExtension.version // remember to bump this version before any release!
    )

    override fun getPluginArtifactForNative(): SubpluginArtifact = SubpluginArtifact(
        groupId = SERIALIZATION_GROUP_NAME,
        artifactId = NATIVE_ARTIFACT_NAME,
        version = gradleExtension.version // remember to bump this version before any release!
    )
}
