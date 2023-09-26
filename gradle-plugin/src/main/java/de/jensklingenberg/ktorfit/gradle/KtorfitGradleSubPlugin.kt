package de.jensklingenberg.ktorfit.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption


open class KtorfitGradleConfiguration {
    /**
     * If the compiler plugin should be active
     */
    var enabled: Boolean = true

    /**
     * version number of the compiler plugin
     */
    @Deprecated("Update the Gradle plugin instead of updating this version")
    var version: String = "1.7.0" // remember to bump this version before any release!

    /**
     * used to get debug information from the compiler plugin
     */
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

    private val Project.kotlinExtension: KotlinProjectExtension?
        get() = this.extensions.findByType<KotlinProjectExtension>()

    private fun Project.getKtorfitConfig() =
        this.extensions.findByType(KtorfitGradleConfiguration::class.java) ?: KtorfitGradleConfiguration()

    override fun apply(project: Project) {
        myproject = project

        with(project) {
            extensions.create(KtorfitGradleSubPlugin.GRADLE_TASKNAME, KtorfitGradleConfiguration::class.java)

            when (val kotlinExtension = this.kotlinExtension) {
                is KotlinSingleTargetExtension<*> -> {
                    dependencies {
                        add("implementation", "de.jensklingenberg.ktorfit:ktorfit-converters-flow:1.8.0")
                    }
                }

                is KotlinMultiplatformExtension -> {
                    dependencies {
                        add("commonMainImplementation", "de.jensklingenberg.ktorfit:ktorfit-converters-flow:1.8.0")
                    }
                }

                else -> { /* Do nothing */
                }
            }


        }


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
