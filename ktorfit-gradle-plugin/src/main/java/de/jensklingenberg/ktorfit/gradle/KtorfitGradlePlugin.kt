package de.jensklingenberg.ktorfit.gradle

import de.jensklingenberg.ktorfit.gradle.KtorfitCompilerSubPlugin.Companion.GROUP_NAME
import de.jensklingenberg.ktorfit.gradle.KtorfitCompilerSubPlugin.Companion.KTORFIT_VERSION
import de.jensklingenberg.ktorfit.gradle.KtorfitCompilerSubPlugin.Companion.SNAPSHOT
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import java.util.Locale.US

class KtorfitGradlePlugin : Plugin<Project> {
    companion object {
        const val GRADLE_TASKNAME = "ktorfit"
    }

    private val Project.kotlinExtension: KotlinProjectExtension?
        get() = this.extensions.findByType<KotlinProjectExtension>()

    private fun Project.getKtorfitConfig() =
        this.extensions.findByType(KtorfitGradleConfiguration::class.java) ?: KtorfitGradleConfiguration()

    override fun apply(project: Project) {
        val ktorfitGradleConfiguration = project.getKtorfitConfig()
        project.pluginManager.apply(KtorfitCompilerSubPlugin::class.java)

        val hasKspApplied = project.extensions.findByName("ksp") != null
        if (hasKspApplied && ktorfitGradleConfiguration.addKspDependencies) {
            val ktorfitKsp = "$GROUP_NAME:ktorfit-ksp"

            val kspPlugin =
                project.plugins.findPlugin("com.google.devtools.ksp") ?: error("KSP plugin not found")

            val kspVersion = "$KTORFIT_VERSION-" + kspPlugin.javaClass.protectionDomain.codeSource.location.toURI().toString()
                .substringAfterLast("-").substringBefore(".jar") + "-SNAPSHOT"

            val dependency = "$ktorfitKsp:$kspVersion$SNAPSHOT"

            when (val kotlinExtension = project.kotlinExtension) {
                is KotlinSingleTargetExtension<*> -> {
                    project.dependencies.add("ksp", dependency)
                }

                is KotlinMultiplatformExtension -> {
                    project.dependencies {
                        add("kspCommonMainMetadata", dependency)
                    }

                    kotlinExtension.targets.configureEach {
                        if (targetName == "metadata") return@configureEach
                        project.dependencies.add(
                            "ksp${
                                targetName.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        US
                                    ) else it.toString()
                                }
                            }", dependency
                        )
                    }

                    kotlinExtension.sourceSets.named("commonMain").configure {
                        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
                    }
                }

                else -> Unit
            }
        }
    }

}
