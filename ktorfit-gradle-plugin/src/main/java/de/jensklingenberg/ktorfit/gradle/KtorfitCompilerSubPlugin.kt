package de.jensklingenberg.ktorfit.gradle

import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.ARTIFACT_NAME
import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.COMPILER_PLUGIN_ID
import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.GROUP_NAME
import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.KTORFIT_VERSION
import de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin.Companion.SNAPSHOT
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

internal class KtorfitCompilerSubPlugin : KotlinCompilerPluginSupportPlugin {

    private lateinit var myproject: Project
    private var gradleExtension: KtorfitGradleConfiguration = KtorfitGradleConfiguration()

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        gradleExtension = kotlinCompilation.target.project.getKtorfitConfig()

        return kotlinCompilation.target.project.provider {
            listOf(
                SubpluginOption("enabled", "true"),
                SubpluginOption("logging", "false")
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
            version = "${KTORFIT_VERSION}-${myproject.kotlinExtension.compilerVersion.get()}${SNAPSHOT}"
        )
    }
}
