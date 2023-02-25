plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("kotlinx-serialization")
}
apply(plugin = "de.jensklingenberg.ktorfit")
version = "1.0-SNAPSHOT"
val ktorVersion = "2.2.3"
configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    enabled = true
    version = "1.0.0"
}
ksp {
    arg("Ktorfit_Errors", "1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
    iosX64()
    iosArm64()
    js(IR) {
        this.nodejs()
        binaries.executable() // not applicable to BOTH, see details below
    }
    linuxX64() {
        binaries {
            executable()
        }
    }

    macosX64("macOS")
    mingwX64()
    sourceSets {
        val commonMain by getting {

            dependencies {
                implementation(project(":ktorfit-lib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

            }
        }
        val linuxX64Main by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core-native:1.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("io.ktor:ktor-client-curl:2.2.3")

            }
        }


        val jvmMain by getting {
               kotlin.srcDir("build/generated/ksp/jvm/jvmMain/")

            dependencies {
                implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.6.4")
                implementation("io.reactivex.rxjava3:rxjava:3.1.6")

                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.4.5")
                implementation("io.ktor:ktor-serialization-gson:$ktorVersion")

            }
        }

        val jvmTest by getting {
            //   kotlin.srcDir("build/kspCaches/jvmMain/")


            dependencies {
                dependsOn(jvmMain)
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
                implementation("junit:junit:4.13.2")


            }
        }

        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")

            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }

    }
}


dependencies {

    add(
        "kspCommonMainMetadata", projects.ktorfitKsp
    )
    add("kspJvm", projects.ktorfitKsp)
    add("kspIosX64", projects.ktorfitKsp)

   // add("kspJvmTest", projects.ktorfitKsp)
    add("kspJs",projects.ktorfitKsp)
    add("kspLinuxX64", projects.ktorfitKsp)
    add("kspMingwX64", projects.ktorfitKsp)

}
