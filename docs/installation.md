#Installation

## Setup
(You can also look how it's done in the [examples](https://github.com/Foso/Ktorfit/tree/master/example))

#### Gradle Plugins
You need to add KSP and the [Ktorfit Gradle plugin](https://plugins.gradle.org/plugin/de.jensklingenberg.ktorfit)
```kotlin
plugins {
  id("com.google.devtools.ksp") version "CURRENT_KSP_VERSION"
  id("de.jensklingenberg.ktorfit") version "KTORFIT_VERSION-KOTLIN-VERSION"
}
```

The Gradle plugin will have **KTORFIT_VERSION-KOTLIN_VERSION** as version number, e.g. {{ktorfit.gradle}}
You can find all available versions [here](https://repo.maven.apache.org/maven2/de/jensklingenberg/ktorfit/ktorfit-gradle-plugin/)

Next you have to add the Ktorfit KSP Plugin to the common target and every compilation target, where you want to use Ktorfit.


```kotlin
val ktorfitVersion = "KTORFIT_VERSION-KSP-VERSION"

dependencies {
    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
    add("ksp[NAMEOFPLATFORM]","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
        ...
}
```
The KSP plugin will have the **KTORFIT_VERSION-KSP_VERSION** as version number, e.g. {{ktorfit.ksp}}. You can find all available versions [here](https://repo.maven.apache.org/maven2/de/jensklingenberg/ktorfit/ktorfit-ksp/)

[NAMEOFPLATFORM] is the name of the compilation target. When you want to use it for the Android module it's **kspAndroid**, for Js it's **kspJs**, etc.
Look here for more information https://kotlinlang.org/docs/ksp-multiplatform.html


#### Ktorfit-lib
Add the Ktorfit-lib to your common module. You can find all available versions [here](https://repo.maven.apache.org/maven2/de/jensklingenberg/ktorfit/ktorfit-lib/)
```kotlin
val ktorfitVersion = "{{ktorfit.release}}"

sourceSets {
    val commonMain by getting{
        dependencies{
            implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
        }
    }
```

#### Ktor
Ktorfit is based on Ktor clients {{ktor.release}}. You don't need to add an extra dependency for the default clients.
When you want to use Ktor plugins for things like serialization, you need to add the dependencies, and they need to be compatible with {{ktor.release}}

You can also use **"de.jensklingenberg.ktorfit:ktorfit-lib-light"**
this will only add the Ktor client core dependency and not the platform dependencies for the clients.
This will give you more control over the used clients, but you have to add them yourself. https://ktor.io/docs/http-client-engines.html
Everything else is the same as "ktorfit-lib"
