# Changelog

All important changes of this project must be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project orients towards [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
Note: This project needs KSP to work and every new Ktorfit with an update of the KSP version is technically a breaking change.
But there is no intent to bump the Ktorfit major version for every KSP update. 

# [2.6.4]()

2.6.4 - 2025-07-29
========================================
* Supported KSP version: >=2.0.2
* Supported Kotlin version: >=2.2.0
* Ktor version: 3.2.1

# Fixed
- ClassCastException in compiler plugin

# [2.6.3]()

2.6.3 - 2025-07-27
========================================
* Supported KSP version: >=2.0.2
* Supported Kotlin version: >=2.2.0
* Ktor version: 3.2.1

# Fixed
- Can't deserialize List on iOS #887
- Java 21 requirement after update to 2.6.0 #883
- Build failed when use parameter with name method #865

Thanks to @Link184 and @king22 for contributing to this release!

# [2.6.2]()

2.6.2 - 2025-07-26
========================================
SKIP THIS VERSION

# [2.6.1]()

2.6.1 - 2025-07-04
========================================
* Supported KSP version: >=2.0.2
* Supported Kotlin version: >=2.2.0
* Ktor version: 3.2.1

## Changed
- Update Ktor to 3.2.1
- Update Kotlin to 2.2.0

Special thanks to @eygraber for contributing to this release!

# [2.6.0]()

2.6.0 - 2025-07-04
========================================
SKIP THIS VERSION, it was released with a wrong Kotlin compiler plugin version.


# [2.5.2]()

2.5.2 - 2025-05-12
========================================
* Supported KSP version: >=1.0.28
* Supported Kotlin version: >=2.1.10
* Ktor version: 3.1.2

## Fixed
-   Resolve KSP version if set with ksp extension #851

Thanks to @sydden1337 for contributing to this release!


# [2.5.1]()

2.5.1 - 2025-04-19
========================================
* Supported KSP version: >=1.0.28
* Supported Kotlin version: >=2.1.10
* Ktor version: 3.1.2

## Fixed
-  Update KSP version check in KtorfitGradlePlugin #842

# [2.5.0]()

2.5.0 - 2025-03-30
========================================
* Supported KSP version: <2.0.0 >=1.0.28
* Supported Kotlin version: >=2.1.10
* Ktor version: 3.1.2

## Added
- Bundle proguard rules for Android

## Changed
- Update Ktor to 3.1.2

## Fixed
- Fixed a release issue where planned changes for 2.4.1 were not included in the release

Thanks to @Goooler for contributing to this release!


# [2.4.1]()

2.4.1 - 2025-03-14
========================================
* Supported KSP version: >=1.0.28
* Supported Kotlin version: >=2.1.10
* Ktor version: 3.1.0

## Fixed
- Ktorfit causes circular task dependency when using KSP2 and Multiplatform Resources library from Jetbrains #876
- TypeInfo kotlinType is null causing deserialization errors #817

Thanks to @alapshin and @griffinsorrentino for contributing to this release!

# [2.4.0]()

2.4.0 - 2025-02-23
========================================
* Supported KSP version: >=1.0.28
* Supported Kotlin version: >=2.1.10
* Ktor version: 3.1.0

## Added
- Add KSP2 support
- Add watchosDeviceArm64 target

# Changed
- Changed internal versioning numbers of the Ktorfit KSP and compiler plugin. 
With that change Ktorfit only needs to be updated when a new KSP / Kotlin version introduces breaking changes in the API and not for every minor update.
This is also the reason why this version has KSP min version 1.0.28 even though the last Ktorfit version has 1.0.30


# [2.3.0]()

2.3.0 - 2025-02-16
========================================
* Supported KSP version: 1.0.30
* Supported Kotlin version: 2.1.10
* Ktor version: 3.1.0

## Added
- Provide option to not use the compiler plugin #764
You can now set the Kotlin version for the compiler plugin. 
By default, it will use the Kotlin version of the project. You can set it to "-" to disable the plugin.

```kotlin
ktorfit{
    kotlinVersion = "-"
    // or
    kotlinVersion = "x.x.x"
}
```

- Added targets:
androidNativeArm32
androidNativeArm64
androidNativeX86
androidNativeX64


- Include function annotations in request attribute
See https://foso.github.io/Ktorfit/requests/#annotations

## Fixed
- @Headers annotation produces unexpected newline in generated code by ksp plugin #752
- Generated code containing repeated @OptIn annotation #767

Thanks to @DatL4g, @dewantawsif and @MohammadFakhraee for contributing to this release!

# [2.2.0]()

2.2.0 - 2024-11-10
========================================

* Supported Kotlin version: 2.0.0; 2.0.10; 2.0.20, 2.1.0-Beta1; 2.0.21-RC, 2.0.21, 2.10
* Supported KSP version: 1.0.27, 1.0.28, 1.0.29, 1.0.30
* Ktor version: 3.0.1

## Changed 
- Ktorfit is now using Ktor 3.0.1 as a default. The extra versions with "-ktor3" will not be updated anymore.
- This also means that now wasmJs is supported by default.

## Fixed
-  Inheritance problem [#663](https://github.com/Foso/Ktorfit/issues/663)
See https://foso.github.io/Ktorfit/generation/#nodelegation

- Generated classes do not propagate opt-in ExperimentalUuidApi [666](https://github.com/Foso/Ktorfit/issues/666) 
 OptIn annotations on interfaces and functions will now be propagated to the generated classes.

- Fixed documentation for converters to match the current version.
- Unresolved reference setBody in generated API implementations [#726](https://github.com/Foso/Ktorfit/issues/726)
- [Android] App crashes if not using Ktor platform client [#720](https://github.com/Foso/Ktorfit/issues/720)

# [2.1.0]()

* Supported Kotlin version: 2.0.0; 2.0.10; 2.0.20, 2.1.0-Beta1; 2.0.21-RC, 2.0.21, 2.1.0-RC, 2.1.0-RC2, 2.1.0
* Supported KSP version: 1.0.24; 1.0.25, 1.0.26, 1.0.27
* Ktor version: 2.3.12; 3.0.0

## Added
- documentation page for [known issues](https://foso.github.io/Ktorfit/knownissues/)

## Changed
- Allow Http Delete with Body [#647](https://github.com/Foso/Ktorfit/issues/647)
- By default, nullable response types will not throw an exception. You can now override this behavior by adding the **DontSwallowExceptionsConverterFactory** 
or your own ConverterFactory to the converterFactories. [#618](https://github.com/Foso/Ktorfit/issues/618)

## Fixed
- Task with path 'kspCommonMainKotlinMetadata' not found in project [#593](https://github.com/Foso/Ktorfit/issues/593)
- Ktorfit Gradle Plugin not compatible with Android Multiplatform Library plugin [#638](https://github.com/Foso/Ktorfit/issues/638)

## Ktor3
The "normal" dependencies will stay on Ktor 2.x till 3.0 is stable. But here are versions that you can use when want to use Ktor3 and WasmJs

de.jensklingenberg.ktorfit:ktorfit-lib-light-ktor-3.0.0:2.1.0

de.jensklingenberg.ktorfit:ktorfit-lib-ktor-3.0.0:2.1.0

de.jensklingenberg.ktorfit:ktorfit-converters-flow-ktor-3.0.0:2.1.0

de.jensklingenberg.ktorfit:ktorfit-converters-call-ktor-3.0.0:2.1.0

de.jensklingenberg.ktorfit:ktorfit-converters-response-ktor-3.0.0:2.1.0

# [2.0.1]()

* Supported Kotlin version: 2.0.0; 2.0.10; 2.0.20
* Supported KSP version: 1.0.24
* Ktor version: 2.3.12

2.0.1 - 2024-08-08
========================================
## Fixed
- Endpoint with types from other module #594 
- Ktorfit plugin doesn't include correct generate source if build directory changes #591
- RequestConverter causing compile error #621 
- Build with Ktor 2.3.12

## ktorfit-annotations
- 2.0.1: The annotations are now also available for WasmJs

## compilerPlugin
- Kotlin 2.0.0: 2.0.1-2.0.0 - 2024-08-08
- Kotlin 2.0.10: 2.0.1-2.0.10 - 2024-08-10
- Kotlin 2.0.20-RC: 2.0.1-2.0.20-RC - 2024-08-13
- Kotlin 2.0.20-RC2: 2.0.1-2.0.20-RC2 - 2024-08-13
- Kotlin 2.0.20: 2.0.1-2.0.20 - 2024-08-23

## ktorfit-ksp
- KSP 1.0.24: ktorfit-ksp-2.0.1-1.0.24 - 2024-08-08

## Ktor3 
The "normal" dependencies will stay on Ktor 2.x till 3.0 is stable. But here are versions that you can use when want to use Ktor3 and WasmJs

| Project                                       |                                                                                                                       Version                                                                                                                       |
|-----------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| ktorfit-lib-light-ktor-3.0.0-beta-2           |           [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-lib-light-ktor-3.0.0-beta-2)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-lib-light-ktor-3.0.0-beta-2)           |
| ktorfit-lib-ktor-3.0.0-beta-2                 |                 [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-lib-ktor-3.0.0-beta-2)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-lib-ktor-3.0.0-beta-2)                 |
| ktorfit-converters-flow-ktor-3.0.0-beta-2     |     [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-converters-flow-ktor-3.0.0-beta-2)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-converters-flow-ktor-3.0.0-beta-2)     |
| ktorfit-converters-call-ktor-3.0.0-beta-2     |     [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-converters-call-ktor-3.0.0-beta-2)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-converters-call-ktor-3.0.0-beta-2)     |
| ktorfit-converters-response-ktor-3.0.0-beta-2 | [![Maven Central](https://img.shields.io/maven-central/v/de.jensklingenberg.ktorfit/ktorfit-converters-response-ktor-3.0.0-beta-2)](https://central.sonatype.com/artifact/de.jensklingenberg.ktorfit/ktorfit-converters-response-ktor-3.0.0-beta-2) |

# [2.0.0]()

ktorfit-ksp-2.0.0-1.0.24 - 2024-08-06
========================================
- Build with KSP 1.0.24

ktorfit-ksp-2.0.0-1.0.23 - 2024-07-14
========================================
- Build with KSP 1.0.23
- 
ktorfit-ksp-2.0.0-1.0.22 - 2024-06-06
========================================
- Build with KSP 1.0.22

2.0.0 - 2024-05-27
========================================

# Changed
- Build with KSP 1.0.21, Kotlin 2.0.0, Ktor 2.3.11
- The needed dependencies for Ktorfit KSP processor are now included in the Ktorfit Gradle plugin. You can remove the ksp() block from your build.gradle.kts file. You still need to apply the KSP plugin. 
  
```kotlin
plugins {
id("com.google.devtools.ksp") version "CURRENT_KSP_VERSION"
id("de.jensklingenberg.ktorfit") version "2.0.0"
}
```

See the installation guide for more information: https://foso.github.io/Ktorfit/installation/

- The code that is generated by KSP is now accessible from the module where the interface is defined. That means code from commonMain can now find the generated code. Generated code from the platform specific code is still only available from the specific modules.
- The **create** function is now deprecated. The reason for that is that it is relying on a compiler plugin to work. This can lead to compile errors when the class can't be found. The plan is to get rid of the plugin. When your project is configured correct, the autocompletion should show an extension function *create* followed by the name of the interface. This function will not trigger the compiler plugin

```kotlin
val api = ktorfit.create<ExampleApi>()
```

replace with:

```kotlin
val api = ktorfit.createExampleApi()
```

### Breaking Changes

The deprecated code got removed.
This will simplify the codebase and make it easier to maintain.
When you haven't used the deprecated converters, there is not much you need to change.
The dependencies for the converters that were previously auto added now need to be added manually.
See the migration guide for more information: https://foso.github.io/Ktorfit/migration/#from-2-to-200

* QualifiedTypeName in Ktorfit

In the previous versions of Ktorfit, the `qualifiedTypename` was always generated in the code. This was used in the `TypeData.createTypeData()` function to provide a fully qualified type name for the data type being used.

```kotlin
val _typeData = TypeData.createTypeData(
    typeInfo = typeInfo<Call<People>>(),
    qualifiedTypename = "de.jensklingenberg.ktorfit.Call<com.example.model.People>"
)
```

In the new version of Ktorfit, this behavior has been changed. Now, by default, Ktorfit will keep `qualifiedTypename` for `TypeData` in the generated code empty. This means that the `qualifiedTypename` will not be automatically generated.

```kotlin
val _typeData = TypeData.createTypeData(
    typeInfo = typeInfo<Call<People>>(),
)
```

However, if you want to keep the old behavior and generate `qualifiedTypename`, you can set it in the Ktorfit config `generateQualifiedTypeName` to `true` in your `build.gradle.kts` file.

```kotlin title="build.gradle.kts"
ktorfit {
  generateQualifiedTypeName = true
}
```

This change was made to provide more flexibility and control to the developers over the generated code. Please update your code accordingly if you were relying on the automatic generation of `qualifiedTypename`.

### Fixed
- Fixes https://github.com/Foso/Ktorfit/issues/548

2.0.0-rc01 - 2024-05-19
========================================
- Build with KSP 1.0.20, Kotlin 2.0.0-RC3, Ktor 2.3.11
- Optimize code generation
- Fixes https://github.com/Foso/Ktorfit/issues/548

2.0.0-beta1 - 2024-04-28
========================================
### Breaking Changes

The deprecated code got removed.
This will simplify the codebase and make it easier to maintain.
When you haven't used the deprecated converters, there is not much you need to change.
Some converters that were previously auto applied now need to be added manually.
See the migration guide for more information: https://foso.github.io/Ktorfit/migration/#from-2-to-200

#### QualifiedTypeName in Ktorfit

In the previous versions of Ktorfit, the `qualifiedTypename` was always generated in the code. This was used in the `TypeData.createTypeData()` function to provide a fully qualified type name for the data type being used.

```kotlin
val _typeData = TypeData.createTypeData(
    typeInfo = typeInfo<Call<People>>(),
    qualifiedTypename = "de.jensklingenberg.ktorfit.Call<com.example.model.People>"
)
```

In the new version of Ktorfit, this behavior has been changed. Now, by default, Ktorfit will keep `qualifiedTypename` for `TypeData` in the generated code empty. This means that the `qualifiedTypename` will not be automatically generated.

```kotlin
val _typeData = TypeData.createTypeData(
    typeInfo = typeInfo<Call<People>>(),
)
```

However, if you want to keep the old behavior and generate `qualifiedTypename`, you can set a KSP argument `Ktorfit_QualifiedTypeName` to `true` in your `build.gradle.kts` file.

```kotlin
ksp {
    arg("Ktorfit_QualifiedTypeName", "true")
}
```

This change was made to provide more flexibility and control to the developers over the generated code. Please update your code accordingly if you were relying on the automatic generation of `qualifiedTypename`.

1.14.0 - 2024-04-15
========================================
- Build with KSP 1.0.20, Kotlin 2.0.0-RC1, Ktor 2.3.10

1.13.0 - 2024-04-14
========================================
- Build with KSP 1.0.20, Kotlin 1.9.23, Ktor 2.3.10

1.12.0 - 2024-01-16
========================================
- Compatible with KSP 1.0.16 and Kotlin 1.9.22


1.11.1 - 2023-12-21
========================================
- Fix compile errors #505 #496

1.11.0 - 2023-12-06
========================================
Compatible with KSP 1.0.15 and Kotlin 1.9.21


### Changed
- KSP 1.0.15 required
- Upgrade dependencies: Ktor 2.3.6

1.10.2 - 2023-11-29
========================================
### Fixed
- fix compile errors because of missing import #490 #486

1.10.1 - 2023-11-15
========================================

### Fixed 
- Using @FieldMap generates uncompiled code due to missing import [#479](https://github.com/Foso/Ktorfit/issues/479)

1.10.0 - 2023-11-06
========================================
Compatible with KSP 1.0.14 and Kotlin 1.9.20

### Added
-  LinuxArm64 support [#475](https://github.com/Foso/Ktorfit/issues/475)

### Changed
- KSP 1.0.14 required
- Optimized code generation

1.9.1 - 2023-10-22
========================================
Compatible with KSP 1.0.13 and Kotlin 1.9.10/1.9.20-RC

When you are still using the Ktorfit Gradle plugin in version 1.0.0, please also update this to 1.9.1

### Added
- Add Tag annotation https://foso.github.io/Ktorfit/requests/#tag

### Changed
- The generated code will not produce warnings anymore


1.8.1 - 2023-10-09
========================================
Compatible with KSP 1.0.13 and Kotlin 1.9.10/1.9.20-Beta2

### Added

### Changed
- Allow nullable body type [#424](https://github.com/Foso/Ktorfit/issues/424)
- Use @Path parameter name as default value [#426](https://github.com/Foso/Ktorfit/issues/426)
- Use @Query parameter name as default value [#428](https://github.com/Foso/Ktorfit/issues/428)
- Use @Field parameter name as default value [#430](https://github.com/Foso/Ktorfit/issues/428)
- You can now also get exceptions like NetworkException with SuspendResponseConverter.[#389](https://github.com/Foso/Ktorfit/issues/389)

### Deprecated
- Deprecated the `convert` function in the `SuspendResponseConverter` interface
See: https://foso.github.io/Ktorfit/migration/#from-170-to-181

! When you still use the deprecated converters because there are use cases which you can't do with the converter factories, please write a GitHub Issue.
The deprecated converters will be removed in one of the upcoming versions

### Removed
### Fixed
### Security

1.8.0 - 2023-10-09
========================================
Skip this version, it had an issue with Kotlin 1.9.20

1.7.0-1.9.20-Beta2 - 2023-09-16
========================================
Version of 1.7.0 that is compatible with Kotlin 1.9.20-Beta2

1.7.0 - 2023-09-16
========================================
# Gradle plugin
From now on with every Ktorfit release there will also be a Gradle plugin with the same version.
That means that drop you can drop the Gradle extension block where you previously set the version number and just bump the number of the Gradle plugin. https://foso.github.io/Ktorfit/installation/

```kotlin
plugins {
id("de.jensklingenberg.ktorfit") version "1.7.0"
}
```

### Added
- Added a compiler type checks if the type used for the create function is an interface

### Changed
- Upgrade dependencies: Ktor 2.3.4

1.6.0 - 2023-08-24
========================================

### Changed
* KSP version 1.9.10-1.0.13 is now required
- Upgrade dependencies: Ktor 2.3.3

1.5.0 - 2023-08-04
========================================

### Changed
* KSP version 1.9.0-1.0.13 is now needed

1.4.4 - 2023-07-26
========================================

### Changed
- Upgrade dependencies: Ktor 2.3.2

1.4.3 - 2023-07-13
========================================

### Fixed
#372 Crash with Xiaomi on create Ktorfit.Builder by @princeparadoxes

1.4.2 - 2023-06-25
========================================

### Fixed
#323 Code generation issue for @Multipart / @FormUrlEncoded by @Ph1ll1pp

1.4.1 - 2023-06-03
========================================

### Changed
- Upgrade dependencies: Ktor 2.3.1

### Fixed
#236 Parsing error for list/array

1.4.0 - 2023-05-27
========================================

### Added
* #85 Added a Response class that can be used as a wrapper around the API Response, the converter for it is automatically applied. thx to @vovahost, @DATL4G

e.g.

```kotlin title=""
interface ExampleApi{
  suspend fun getUser(): Response<User>
}

val user = userKtorfit.create<ExampleApi>().getUser()
    
if(user.isSuccessful){
  user.body()
}else{
  user.errorBody()
}
```

* Ktorfit is now using converters factories to apply the converters, similar to Retrofit 
 see more here https://foso.github.io/Ktorfit/converters/converters/
* TypeData now has a field "typeInfo" can be used to convert the Ktor HttpResponse body to the wanted type
* CallConverterFactory for replacement of CallResponseConverter
* FlowConverterFactory for replacement of FlowResponseConverter

* Added support for targets:
macosArm64, tvosArm64, tvosX64, tvosSimulatorArm64, watchosSimulatorArm64 #315

### Changed
- Upgrade dependencies: Kotlin 1.8.21

### Deprecated
* ResponseConverter, use Converter.ResponseConverter instead
* SuspendResponseConverter, use Converter.SuspendResponseConverter instead
* RequestConverter, use Converter.RequestParameterConverter instead
* See also: https://foso.github.io/Ktorfit/converters/migration/

1.3.0 - 2023-05-14
========================================

### Changed
* Optimized generated code, the generated code that is used for a request will 
 now directly set the Ktor code instead of delegating it to a Ktorfit class. This will
 make the code easier to understand.

* KSP version 1.0.11 is now needed

### Fixed
[Bug]: IllegalArgumentException with Custom Http Annotation #274

### Bumped
KSP version to 1.0.11

1.2.0 - 2023-05-05
========================================

### Bumped
Now based on Ktor 2.3.0

1.1.0 - 2023-04-15
========================================

### Added
From now on there are two versions of the ktorfit-lib.

"de.jensklingenberg.ktorfit:ktorfit-lib"
will stay like before and add the platform client dependencies for the clients.

"de.jensklingenberg.ktorfit:ktorfit-lib-light"
this will only add the client core dependency and not the platform dependencies for the clients.
This will give you more control over the used clients, but you have to add them yourself. https://ktor.io/docs/http-client-engines.html
Everything else is the same as "ktorfit-lib"

### Changed
* Kotlin version 1.8.20 is now needed
* KSP version 1.8.20-1.0.10 is now needed

### Bumped
* Kotlin to 1.8.20
* KSP version to 1.8.20-1.0.10

1.0.1 - 2023-03-20
========================================

### Added
From now on there are two versions of the ktorfit-lib. 

"de.jensklingenberg.ktorfit:ktorfit-lib"
will stay like before and add the platform client dependencies for the clients.

"de.jensklingenberg.ktorfit:ktorfit-lib-light"
this will only add the client core dependency and not the platform dependencies for the clients.
This will give you more control over the used clients, but you have to add them yourself. https://ktor.io/docs/http-client-engines.html
Everything else is the same as "ktorfit-lib"

### Fixed
[Bug]: Post request body serialization doesn't work #202

---

1.0.0 - 2023-03-02
========================================
This project is now following [semver](https://semver.org/)

### Added
* internal optimizations
* throw compile error when generated class can not be found

### Fixed

* Timeout throws exception outside of scope of SuspendResponseConverter #127
* Fix broken/outdated docs link (#140) by @T-Spoon

### Bumped

- based on Ktor 2.2.4

1.0.0-beta18 (12-02-2023)
========================================
NEW:

* You can now disable the check if the baseUrl ends with a /

```kotlin
Ktorfit.Builder().baseUrl(testBaseUrl, checkUrl = false).build()
```

🐛 Bugs fixed

* Fixed Ktorfit breaking incremental compilation #110

⬆️ Deps updates

- based on Ktor 2.2.3

1.0.0-beta17 (21-01-2023)
========================================
💥 Breaking changes:

- Ktorfit now needs an additional gradle plugin. This will solve several issues with multi-module projects.

Add this [plugin](https://plugins.gradle.org/plugin/de.jensklingenberg.ktorfit):
```kotlin
plugins {
  id "de.jensklingenberg.ktorfit" version "1.0.0"
}
```

NEW:

* interfaces can now be internal

🐛 Bugs fixed

* Ktorfit multiple module support #92
* Add support for 'internal' parameter type #13
* Duplicate class KtorfitExtKt found in modules moduleA and moduleB #86
*  Android overload resolution ambiguity #64
*  Form data is double encoded #95

⬆️ Deps updates

- based on Ktor 2.2.2
- Kotlin 1.8.0
- KSP 1.8.0-1.0.8
- update Android TargetSdk to 33


1.0.0-beta16 (13-11-2022)
========================================

NEW:
- Field parameters can now be nullable, null values will be ignored in requests
- Add option to turn of error checking

  **ksp {
  arg("Ktorfit_Errors", "1")
  }**
  
  You can set it in your build.gradle.kts file, 
  
  0: Turn off all Ktorfit related error checking
  
  1: Check for errors
  
  2: Turn errors into warnings
  
-  Added RequestConverter support #84

⬆️ Deps updates

- based on Ktor 2.1.3
- Kotlin 1.7.21
- KSP 1.0.8
- update Android TargetSdk to 33

🐛 Bugs fixed

- FlowResponseConverter #81

## What's Changed
* build(deps): bump logback-classic from 1.4.0 to 1.4.3 by @dependabot in https://github.com/Foso/Ktorfit/pull/74
* Foso/revert converters changes by @Foso in https://github.com/Foso/Ktorfit/pull/76
* 67 add nullable field parameters support by @Foso in https://github.com/Foso/Ktorfit/pull/80
* fix: FlowResponseConverter by @Foso in https://github.com/Foso/Ktorfit/pull/81
* Added RequestConverter support by @DATL4G in https://github.com/Foso/Ktorfit/pull/84
* feat: add option to turn off error checking #77 by @Foso in https://github.com/Foso/Ktorfit/pull/88


**Full Changelog**: https://github.com/Foso/Ktorfit/compare/v1.0.0-beta15...v1.0.0-beta16

1.0.0-beta15 (05-10-2022)
========================================
⬆️ Deps updates

- based on Ktor 2.1.2

🐛 Bugs fixed

- kotlinx.coroutines.JobCancellationException: Parent job is Completed #70

💥 Breaking changes

- reverted the api of converters to the state of beta13, see #71 
- when you are updating from beta13, this is the only change to converters:
  returnTypeName is replaced through typeData, you can use typeData.qualifiedName to get the same value as returnTypeName


1.0.0-beta14 (24-09-2022)
========================================
NEW:
- Query parameters can now be nullable, null values will be ignored in requests
- Function return types can now be nullable

FIX:
- Url annotation not resolved correctly #65

BREAKING CHANGES:
- Changed naming of Converters:

  - SuspendResponseConverter:
    - is now called RequestConverter
    - the wrapSuspendResponse is now called convertRequest.
    - returnTypeName is replaced through typeData, you can use typeData.qualifiedName to get the same value as returnTypeName
      RequestConverter need to be added with the requestConverter() on your Ktorfit object.
    - [https://foso.github.io/Ktorfit/requestconverter/](https://foso.github.io/Ktorfit/requestconverter/)
  - ResponseConverters:
    - returnTypeName is replaced through typeData, you can use typeData.qualifiedName to get the same value as returnTypeName
      [https://foso.github.io/Ktorfit/responseconverter/](https://foso.github.io/Ktorfit/responseconverter/)


1.0.0-beta13 (10-09-2022)
========================================
- KtorfitCallResponseConverter and KtorfitSuspendCallResponseConverter are now combined in KtorfitCallResponseConverter
- based on Ktor 2.1.1

Fixed:
- Url annotation not resolved correctly #52

1.0.0-beta12 (31-08-2022)
========================================
## Breaking Changes: 
wrapResponse from SuspendResponseConverter got renamed to wrapSuspendResponse. This add the possibility to have ResponseConverter and SuspendResponseConverter implemented in the same class.

## Changes: 
- throw compiler time error when you use @Path without the corresponding value inside the relative url path
- every generated implementation class of an interface that Ktorfit generates will now contain a "create" ext function that can be used instead of the generic create() function
e.g. Let's say you have a interface GithubService, then you can create an instance like this:

```kotlin
val ktorfit = ktorfit {
baseUrl("http://example.com/")
}.create<GithubService>()
```
or this

```kotlin
val ktorfit = ktorfit {
baseUrl("http://example.com/")
}.createGithubService()
```

By default, IntelliJ/Android Studio can't find the generated code, you need to add the KSP generated folder to the sourcesets 
like this: (See more here: https://kotlinlang.org/docs/ksp-quickstart.html#make-ide-aware-of-generated-code)

```kotlin
kotlin.srcDir("build/generated/ksp/jvm/jvmMain/")
```


1.0.0-beta11 (21-08-2022)
========================================

- you can now use ResponseConverter in combination with suspend functions. Implement the SuspendResponseConverter
- KtorfitCallResponseConverter and FlowResponseConverter moved to de.jensklingenberg.ktorfit.converter.builtin


1.0.0-beta10 (18-08-2022)
========================================

- based on Ktor 2.0.2
- added windows target #26
- @PATCH, @POST, @PUT now have a default value #22
- Ktorfit now uses a builder pattern for setup
 e.g. change this: 
 ```kotlin
 Ktorfit("https://example.com/", HttpClient {})
```
 

to this: 

```kotlin
Ktorfit.Builder()
.baseUrl("https://example.com/")
.httpClient(HttpClient {})
.build()
```

- 
## Breaking Changes:
@Headers now requires a vararg of String instead of an Array
e.g. you need to change from:

```kotlin
@Headers(
["Authorization: token ghp_abcdefgh",
"Content-Type: application/json"]
)
```


to this:
```kotlin
@Headers(
"Authorization: token ghp_abcdefgh",
"Content-Type: application/json"
)
```


1.0.0-beta09
========================================
- #15 fix encoding of query parameters

1.0.0-beta08
========================================
- fix issue with Koin Annotations


1.0.0-beta07
========================================
- fix issue with FormUrlEncoded
- based on Ktor 2.0.2

1.0.0-beta06
========================================
- fix issue with KSP 1.0.5 #19

1.0.0-beta05
========================================
- fixed: Custom Http Method with @Body is now possible #6
- based on Ktor 2.0.1
- cleanup example project @mattrob33

1.0.0-beta04
========================================
initial release
