plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("kotlinx-serialization")
}

version = "1.0-SNAPSHOT"
val ktorVersion = "2.1.2"

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
                implementation("io.ktor:ktor-client-core-native:1.3.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("io.ktor:ktor-client-curl:2.1.1")

            }
        }


        val jvmMain by getting {
               kotlin.srcDir("build/generated/ksp/jvm/jvmMain/")

            dependencies {
                implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.6.4")
                implementation("io.reactivex.rxjava3:rxjava:3.1.5")

                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.4.0")
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

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
    }
}
dependencies {
    add(
        "kspCommonMainMetadata",
        project(":ktorfit-ksp")
    )
    add("kspJvm", project(":ktorfit-ksp"))
    add("kspIosX64", project(":ktorfit-ksp"))

   // add("kspJvmTest", project(":ktorfit-ksp"))
    add("kspJs", project(":ktorfit-ksp"))
    add("kspLinuxX64", project(":ktorfit-ksp"))
    add("kspMingwX64", project(":ktorfit-ksp"))

}

