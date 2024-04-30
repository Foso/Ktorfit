<h1>Ktorfit</h1>

[![All Contribtors](https://img.shields.io/badge/Maven-Central-download.svg?style=flat-square)](https://mvnrepository.com/artifact/de.jensklingenberg.ktorfit) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Foso/Ktorfit)
[![jCenter](https://img.shields.io/badge/Apache-2.0-green.svg)](https://github.com/Foso/Ktorfit/blob/master/LICENSE)
[Documentation](http://foso.github.io/Ktorfit)

[![Platforms](https://raw.githubusercontent.com/Foso/Ktorfit/master/docs/assets/badges/platforms.svg)](https://raw.githubusercontent.com/Foso/Ktorfit/master/docs/assets/badges/platforms.svg)
<p align="center">
  <img src ="https://raw.githubusercontent.com/Foso/Experimental/master/carbon.png"  />
</p>

## Introduction

Ktorfit is a HTTP client/Kotlin Symbol Processor for Kotlin Multiplatform ( Android, iOS, Js, Jvm, Linux)
using [KSP](https://github.com/google/ksp) and [Ktor clients](https://ktor.io/docs/getting-started-ktor-client.html)
inspired by [Retrofit](https://square.github.io/retrofit/)

## Show some :heart: and star the repo to support the project

[![GitHub stars](https://img.shields.io/github/stars/Foso/Ktorfit.svg?style=social&label=Star)](https://github.com/Foso/Ktorfit) [![GitHub forks](https://img.shields.io/github/forks/Foso/Ktorfit.svg?style=social&label=Fork)](https://github.com/Foso/Ktorfit/fork) [![Twitter Follow](https://img.shields.io/twitter/follow/jklingenberg_.svg?style=social)](https://twitter.com/jklingenberg_)

## How to use

Please see the documentation at [https://foso.github.io/Ktorfit/](https://foso.github.io/Ktorfit/)

## Compatibility
See https://foso.github.io/Ktorfit/#compatibility

# Release

build.gradle.kts:

```kotlin
plugins {
    id("de.jensklingenberg.ktorfit") version "2.0.0-beta1"
}
```

build.gradle

```kotlin
plugins {
    id("de.jensklingenberg.ktorfit") version "2.0.0-beta1"
}
```

KSP:

```kotlin
de.jensklingenberg.ktorfit:ktorfit-ksp:2.0.0-beta1
```

Ktorfit-lib/-light:

```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-lib:2.0.0-beta1")
or
implementation("de.jensklingenberg.ktorfit:ktorfit-lib-light:2.0.0-beta1")
```

## 👷 Project Structure

* <kbd>compiler plugin</kbd> - module with source for the compiler plugin
* <kbd>ktorfit-annotations</kbd> - module with annotations for the Ktorfit
* <kbd>ktorfit-ksp</kbd> - module with source for the KSP plugin
* <kbd>ktorfit-lib-core</kbd> - module with source for the Ktorfit lib
* <kbd>ktorfit-lib</kbd> - ktorfit-lib-core + dependencies on platform specific clients
* <kbd>sandbox</kbd> - experimental test module to try various stuff

* <kbd>example</kbd> - contains example projects that use Ktorfit
* <kbd>docs</kbd> - contains the source for the GitHub page

## ✍️ Feedback

Feel free to send feedback on [Twitter](https://twitter.com/jklingenberg_)
or [file an issue](https://github.com/foso/Ktorfit/issues/new). Feature requests/Pull Requests are always welcome.

## Acknowledgments

Some parts of this project are reusing ideas that are originally coming
from [Retrofit](https://square.github.io/retrofit/) from [Square](https://github.com/square). Thank you for Retrofit!

Thanks to JetBrains for Ktor and Kotlin!

## Credits

Ktorfit is brought to you by these [contributors](https://github.com/Foso/Ktorfit/graphs/contributors).

## 📜 License

This project is licensed under the Apache License, Version 2.0 - see
the [LICENSE.md](https://github.com/Foso/Ktorfit/blob/master/LICENSE) file for details

