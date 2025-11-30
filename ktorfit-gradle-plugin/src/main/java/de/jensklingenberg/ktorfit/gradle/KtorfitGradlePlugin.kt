package de.jensklingenberg.ktorfit.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.util.Locale.US
import kotlin.reflect.full.declaredMemberProperties

class KtorfitGradlePlugin : Plugin<Project> {
    companion object {
        const val GRADLE_TASKNAME = "ktorfit"
        const val GROUP_NAME = "de.jensklingenberg.ktorfit"
        const val ARTIFACT_NAME = "compiler-plugin"
        const val COMPILER_PLUGIN_ID = "ktorfitPlugin"
        const val KTORFIT_KSP_PLUGIN_VERSION = "2.6.4"
        const val KTORFIT_COMPILER_PLUGIN_VERSION = "2.3.1"
        const val MIN_KSP_VERSION = "2.0.2"
        const val MIN_KOTLIN_VERSION = "2.2.0"
    }

    override fun apply(project: Project) {
        with(project) {
            val extension = project.ktorfitExtension(GRADLE_TASKNAME)

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
                        .removeSuffix("-SNAPSHOT")
                        .substringAfterLast("-")
                        .substringBefore(".jar")

                checkKSPVersion(kspVersion)
                val kspExtension = extensions.findByName("ksp") ?: error("KSP config not found")
                val argMethod = kspExtension.javaClass.getMethod("arg", String::class.java, String::class.java)

                afterEvaluate {
                    val errorCheckingMode = extension.errorCheckingMode.getOrElse(ErrorCheckingMode.ERROR)
                    val generateQualifiedTypeName = extension.generateQualifiedTypeName.getOrElse(false)

                    argMethod.invoke(kspExtension, "Ktorfit_Errors", errorCheckingMode.ordinal.toString())
                    argMethod.invoke(
                        kspExtension,
                        "Ktorfit_QualifiedTypeName",
                        generateQualifiedTypeName.toString(),
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
                            val useKsp2 =
                                kspExtension.javaClass.kotlin.declaredMemberProperties.find {
                                    it.name == "useKsp2"
                                }?.call(kspExtension).let {
                                    (it as Property<*>?)?.get() as Boolean?
                                } ?: project.findProperty("ksp.useKSP2")?.toString()?.toBoolean() ?: false

                            if (useKsp2) {
                                tasks.named { name -> name.startsWith("ksp") }.configureEach {
                                    if (name != "kspCommonMainKotlinMetadata") {
                                        dependsOn("kspCommonMainKotlinMetadata")
                                    }
                                }
                            } else {
                                tasks.withType(KotlinCompilationTask::class.java).configureEach {
                                    if (name != "kspCommonMainKotlinMetadata") {
                                        dependsOn("kspCommonMainKotlinMetadata")
                                    }
                                }
                            }
                        }
                    }
                }
                val dependency = "$ktorfitKsp:$KTORFIT_KSP_PLUGIN_VERSION"

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

                            if (this.name == "android")
                                {
                                    // Fix android as single target in multiplatform projects
                                    dependencies.add("ksp", dependency)
                                }
                        }

                        kotlinExtension.sourceSets
                            .named(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)
                            .configure {
                                kotlin.srcDir(
                                    "${layout.buildDirectory.get()}/generated/ksp/metadata/" +
                                        "${KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME}/kotlin"
                                )
                            }
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun checkKSPVersion(kspVersion: String) {
        val kspParts = kspVersion.split(".").map { it.toInt() }
        val minParts = MIN_KSP_VERSION.split(".").map { it.toInt() }
        for (i in 0 until minOf(kspParts.size, minParts.size)) {
            if (kspParts[i] > minParts[i]) {
                return
            } else if (kspParts[i] < minParts[i]) {
                error("Ktorfit: KSP version $kspVersion is not supported. You need at least version $MIN_KSP_VERSION")
            }
        }
    }
}

/**
 * Retrieve the Ktorfit plugin extension.
 *
 * Tries to find it by type or creates it otherwise.
 */
internal fun Project.ktorfitExtension(name: String = KtorfitGradlePlugin.GRADLE_TASKNAME): KtorfitPluginExtension {
    return this.extensions.findByType<KtorfitPluginExtension>()
        ?: runCatching { this@ktorfitExtension.createKtorfitExtension(name) }.getOrNull()
        ?: this.extensions.findByName(name) as? KtorfitPluginExtension
        ?: this.extensions.getByType<KtorfitPluginExtension>()
}

/**
 * Creates the Ktorfit plugin extension.
 *
 * @see KtorfitPluginExtension
 * @throws IllegalArgumentException When an extension with the given name already exists.
 */
@Throws(IllegalArgumentException::class)
private fun Project.createKtorfitExtension(name: String = KtorfitGradlePlugin.GRADLE_TASKNAME): KtorfitPluginExtension {
    return this@createKtorfitExtension.extensions.create(
        name = name,
        type = KtorfitPluginExtension::class,
    ).apply { setupConvention(this@createKtorfitExtension) }
}

internal val KotlinProjectExtension.targets: Iterable<KotlinTarget>
    get() =
        when (this) {
            is KotlinSingleTargetExtension<*> -> listOf(this.target)
            is KotlinMultiplatformExtension -> targets
            else -> error("Unexpected 'kotlin' extension $this")
        }
