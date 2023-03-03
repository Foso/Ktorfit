#Installation

## Setup
(You can also look how it's done in the [examples](https://github.com/Foso/Ktorfit/tree/master/example))

#### Gradle Plugins
You need to add KSP and the [Ktorfit Gradle plugin](https://plugins.gradle.org/plugin/de.jensklingenberg.ktorfit)
```kotlin
plugins {
  id("com.google.devtools.ksp") version "CURRENT_KSP_VERSION"
  id("de.jensklingenberg.ktorfit") version "1.0.0"
}

configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    version = "1.0.0"
}
```

Next you have to add the Ktorfit KSP Plugin to the common target and every compilation target, where you want to use Ktorfit.


```kotlin
val ktorfitVersion = "1.0.0"

dependencies {
    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
    add("ksp[NAMEOFPLATFORM]","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
        ...
}
```

[NAMEOFPLATFORM] is the name of the compilation target. When you want to use it for the Android module it's **kspAndroid**, for Js it's **kspJs**, etc.
Look here for more information https://kotlinlang.org/docs/ksp-multiplatform.html


#### Ktorfit-lib

Add the Ktorfit-lib to your common module.
```kotlin
val ktorfitVersion = "1.0.0"

sourceSets {
    val commonMain by getting{
        dependencies{
            implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
        }
    }
```

#### Ktor
Ktorfit is based on Ktor clients 2.2.4. You don't need to add an extra dependency for the default clients.
When you want to use Ktor plugins for things like serialization, you need to add the dependencies, and they need to be compatible with 2.2.4
