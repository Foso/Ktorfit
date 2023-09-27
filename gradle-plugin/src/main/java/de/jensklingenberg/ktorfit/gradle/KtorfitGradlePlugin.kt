package de.jensklingenberg.ktorfit.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension



class KtorfitGradlePlugin : Plugin<Project> {
    companion object {
        const val GRADLE_TASKNAME = "ktorfit"
    }

    private val Project.kotlinExtension: KotlinProjectExtension?
        get() = this.extensions.findByType<KotlinProjectExtension>()

    override fun apply(project: Project) {

        with(project) {
            extensions.create(GRADLE_TASKNAME, KtorfitGradleConfiguration::class.java)

            val flowConverterDependencyNotation = "de.jensklingenberg.ktorfit:ktorfit-converters-flow:1.7.0"
            when (kotlinExtension) {
                is KotlinSingleTargetExtension<*> -> {
                    dependencies {
                        add("implementation", flowConverterDependencyNotation)
                    }
                }

                is KotlinMultiplatformExtension -> {
                    dependencies {
                        add("commonMainImplementation", flowConverterDependencyNotation)
                    }
                }

                else -> { /* Do nothing */
                }
            }
        }
        project.pluginManager.apply(KtorfitCompilerSubPlugin::class.java)
    }


}