package de.jensklingenberg.ktorfit.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.util.Locale.US

class KtorfitGradlePlugin : Plugin<Project> {
    companion object {
        const val GRADLE_TASKNAME = "ktorfit"
        const val GROUP_NAME = "de.jensklingenberg.ktorfit"
        const val ARTIFACT_NAME = "compiler-plugin"
        const val COMPILER_PLUGIN_ID = "ktorfitPlugin"
        const val KTORFIT_VERSION = "2.0.1" // remember to bump this version before any release!
        const val SNAPSHOT = ""
        const val MIN_KSP_VERSION = "1.0.24"
        const val MIN_KOTLIN_VERSION = "2.0.0"
    }

    override fun apply(project: Project) {
        with(project) {
            extensions.create(GRADLE_TASKNAME, KtorfitGradleConfiguration::class.java)

            pluginManager.apply(KtorfitCompilerSubPlugin::class.java)

            val hasKspApplied = extensions.findByName("ksp") != null
            if (hasKspApplied) {
                val ktorfitKsp = "$GROUP_NAME:ktorfit-ksp"

                val kspPlugin =
                    plugins.findPlugin("com.google.devtools.ksp") ?: error("KSP plugin not found")

                val kspVersion =
                    kspPlugin.javaClass.protectionDomain.codeSource.location
                        .toURI()
                        .toString()
                        .substringAfterLast("-")
                        .substringBefore(".jar")

                checkKSPVersion(kspVersion)
                val kspExtension = extensions.findByName("ksp") ?: error("KSP config not found")
                val argMethod = kspExtension.javaClass.getMethod("arg", String::class.java, String::class.java)

                afterEvaluate {
                    val config = getKtorfitConfig()

                    argMethod.invoke(kspExtension, "Ktorfit_Errors", config.errorCheckingMode.ordinal.toString())
                    argMethod.invoke(
                        kspExtension,
                        "Ktorfit_QualifiedTypeName",
                        config.generateQualifiedTypeName.toString(),
                    )

                    /**
                     * This is currently a workaround for a bug in KSP that causes the plugin
                     * to not work with multiplatform projects with only one target.
                     * https://github.com/google/ksp/issues/1525
                     */
                    val singleTarget =
                        project.kotlinExtension.targets
                            .toList()
                            .size == 2

                    if (kotlinExtension is KotlinMultiplatformExtension) {
                        if (singleTarget) {
                            argMethod.invoke(kspExtension, "Ktorfit_MultiplatformWithSingleTarget", true.toString())
                        } else {
                            tasks.withType(KotlinCompilationTask::class.java).configureEach {
                                if (name != "kspCommonMainKotlinMetadata") {
                                    dependsOn("kspCommonMainKotlinMetadata")
                                }
                            }
                        }
                    }
                }
                val dependency = "$ktorfitKsp:$KTORFIT_VERSION-$kspVersion$SNAPSHOT"

                when (val kotlinExtension = kotlinExtension) {
                    is KotlinSingleTargetExtension<*> -> {
                        dependencies.add("ksp", dependency)
                    }

                    is KotlinMultiplatformExtension -> {
                        kotlinExtension.targets.configureEach {
                            if (platformType.name == "common") {
                                dependencies.add("kspCommonMainMetadata", dependency)
                                return@configureEach
                            }
                            val capitalizedTargetName =
                                targetName.replaceFirstChar {
                                    if (it.isLowerCase()) {
                                        it.titlecase(
                                            US,
                                        )
                                    } else {
                                        it.toString()
                                    }
                                }
                            dependencies.add("ksp$capitalizedTargetName", dependency)

                            if (this.compilations.any { it.name == "test" }) {
                                dependencies.add("ksp${capitalizedTargetName}Test", dependency)
                            }
                        }

                        kotlinExtension.sourceSets.named("commonMain").configure {
                            kotlin.srcDir("${layout.buildDirectory.get()}/generated/ksp/metadata/commonMain/kotlin")
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun checkKSPVersion(kspVersion: String) {
        val kspVersionParts = kspVersion.split(".")
        if (kspVersionParts[2].toInt() < MIN_KSP_VERSION.split(".")[2].toInt()) {
            error("Ktorfit: KSP version $kspVersion is not supported. You need at least version $MIN_KSP_VERSION")
        }
    }
}

internal fun Project.getKtorfitConfig() = this.extensions.findByType(KtorfitGradleConfiguration::class.java) ?: KtorfitGradleConfiguration()
