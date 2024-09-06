<h1>Ktorfit</h1>

[![Maven](https://img.shields.io/badge/Maven-Central-download.svg?style=flat-square)](https://central.sonatype.com/search?q=g:de.jensklingenberg.ktorfit) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Foso/Ktorfit)
[![License](https://img.shields.io/badge/Apache-2.0-green.svg)](https://github.com/Foso/Ktorfit/blob/master/LICENSE)
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

## Ktorfit Packages

| Project                     |                                                                                                                  Version                                                                                                                  |
|-----------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| Ktorfit Gradle Plugin       | [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/de.jensklingenberg.ktorfit.gradle.plugin)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/de.jensklingenberg.ktorfit.gradle.plugin) |
| ktorfit-lib                 |                              [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-lib)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-lib)                              |
| ktorfit-lib-light           |                        [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-lib-light)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-lib-light)                        |
| compiler-plugin             |                          [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/compiler-plugin)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/compiler-plugin)                          |
| ktorfit-ksp                 |                              [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-lib)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-ksp)                              |
| ktorfit-converters-flow     |                  [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-converters-flow)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-converters-flow)                  |
| ktorfit-converters-call     |                  [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-converters-call)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-converters-call)                  |
| ktorfit-converters-response |              [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-converters-response)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-converters-response)              |

## Ktorfit Ktor 3 Packages
The main dependencies will stay on Ktor 2.x till Ktor 3 is stable.
When you want to use Ktor 3 and WasmJs, you need to replace your dependencies with a "ktor3" version, you can use the following packages:

| Project                           |                                                                                                                       Version                                                                                                                       |
|-----------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| ktorfit-lib-light-ktor-3.0.0-beta-2           |           [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-lib-light-ktor-3.0.0-beta-2)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-lib-light-ktor-3.0.0-beta-2)           |
| ktorfit-lib-ktor-3.0.0-beta-2                 |                 [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-lib-ktor-3.0.0-beta-2)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-lib-ktor-3.0.0-beta-2)                 |
| ktorfit-converters-flow-ktor-3.0.0-beta-2     |     [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-converters-flow-ktor-3.0.0-beta-2)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-converters-flow-ktor-3.0.0-beta-2)     |
| ktorfit-converters-call-ktor-3.0.0-beta-2     |     [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-converters-call-ktor-3.0.0-beta-2)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-converters-call-ktor-3.0.0-beta-2)     |
| ktorfit-converters-response-ktor-3.0.0-beta-2 | [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-converters-response-ktor-3.0.0-beta-2)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-converters-response-ktor-3.0.0-beta-2) |

You can find all Ktorfit packages on [Maven Central](https://search.maven.org/search?q=de.jensklingenberg.ktorfit).

🔎 Check the [latest changes](https://github.com/Foso/Ktorfit/blob/master/docs/CHANGELOG.md) to update your project.

🛠 Follow the [setup page](https://foso.github.io/Ktorfit/installation/) for more details

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

