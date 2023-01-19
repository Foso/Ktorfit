# Gradle plugin
The Gradle plugin is needed to easily add the compiler plugin to the project and pass data to it.

Add this plugin from Gradle plugin portal:

```kotlin
plugins {
    id "de.jensklingenberg.ktorfit" version "1.0.0"
}
```

The plugin can be configured:

```kotlin
configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    enabled = true
    version = "1.0.0-beta17"
}
```
