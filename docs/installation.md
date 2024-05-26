#Installation

## Setup
(You can also look how it's done in the [examples](https://github.com/Foso/Ktorfit/tree/master/example))

#### Gradle Plugins
You need to add KSP and the [Ktorfit Gradle plugin](https://plugins.gradle.org/plugin/de.jensklingenberg.ktorfit)
```kotlin
plugins {
  id("com.google.devtools.ksp") version "CURRENT_KSP_VERSION"
  id("de.jensklingenberg.ktorfit") version "{{ktorfit.release}}"
}
```

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

You can also use **"de.jensklingenberg.ktorfit:ktorfit-lib-light"**
this will only add the Ktor client core dependency and not the platform dependencies for the clients.
This will give you more control over the used clients, but you have to add them yourself. https://ktor.io/docs/http-client-engines.html
Everything else is the same as "ktorfit-lib"

#### Ktor
Ktorfit is based on Ktor clients {{ktor.release}}. You don't need to add an extra dependency for the default clients.
When you want to use Ktor plugins for things like serialization, you need to add the dependencies, and they need to be compatible with {{ktor.release}}


