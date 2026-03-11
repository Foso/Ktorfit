# Ktorfit Build Warnings Plan

**Date:** 2026-03-11
**Branch:** `modernization`
**Build result:** SUCCESS
**Kotlin version:** 2.3.20-RC3

---

## Summary

The build produces warnings in the following categories:

| # | Category | Count | Severity | Effort |
|---|----------|-------|----------|--------|
| 1 | Kotlin plugin version mismatch | 1 | Low | N/A |
| 2 | `-Xjvm-default` deprecated | 1 | Medium | Easy |
| 3 | `ExperimentalCompilerApi` unresolved opt-in | 1 | Medium | Easy |
| 4 | Deprecated `referenceClass` API (compiler plugin) | 1 | High | Medium |
| 5 | Deprecated `TypeInfo` constructor & `platformType` | 2 (×many targets) | Medium | Easy |
| 6 | Deprecated `create()` in tests and sandbox | ~10 | Low | Easy |

---

## Detailed Plan

### 1. Kotlin plugin version mismatch

**Warning:**
```
WARNING: Unsupported Kotlin plugin version.
The `embedded-kotlin` and `kotlin-dsl` plugins rely on features of Kotlin `2.3.0`
that might work differently than in the requested version `2.3.20-RC3`.
```

**Source:** `ktorfit-gradle-plugin` module (uses `kotlin-dsl` plugin alongside `kotlin(jvm)`)

**Root cause:** The Gradle-bundled `kotlin-dsl` plugin uses Kotlin 2.3.0 (from Gradle 9.4.0), but the project applies Kotlin 2.3.20-RC3 in the same module.

**Fix:** No action needed — this will resolve itself when Gradle ships with a matching Kotlin version. This is expected when using Kotlin RC/beta versions.

---

### 2. `-Xjvm-default` deprecated

**Warning:**
```
w: -Xjvm-default is deprecated. Use -jvm-default instead.
```

**Source:** `:ktorfit-gradle-plugin:kaptGenerateStubsKotlin` task

**Root cause:** This flag is likely being set internally by the `kotlin-dsl` or kapt plugin, not by the project itself. No explicit `-Xjvm-default` was found in project build files.

**Fix:** This is upstream (Gradle/kapt) behavior. No direct action possible. Will likely be fixed in a future Gradle or kapt update.

---

### 3. `ExperimentalCompilerApi` unresolved opt-in

**Warning:**
```
w: Opt-in requirement marker 'org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi'
   is unresolved. Make sure it's present in the module dependencies.
```

**Source:** `:ktorfit-ksp:compileKotlin`

**Root cause:** The KSP module opts into `ExperimentalCompilerApi` but doesn't have the dependency that provides this annotation on the compile classpath.

**Fix:** Either:
- Add the `org.jetbrains.kotlin:kotlin-compiler-embeddable` (or the specific artifact providing this annotation) as a `compileOnly` dependency to the `ktorfit-ksp` module, OR
- Remove the opt-in if it's no longer needed (the annotation may have been stabilized in newer Kotlin versions)

---

### 4. Deprecated `referenceClass` API (compiler plugin)

**Warning:**
```
w: .../CreateFuncTransformer.kt:86:35
   'fun referenceClass(classId: ClassId): IrClassSymbol?' is deprecated.
   Please use `finderForBuiltins()` or `finderForSource(fromFile)` instead.
```

**Source:** `ktorfit-compiler-plugin/src/main/java/de/jensklingenberg/ktorfit/CreateFuncTransformer.kt:86`

**Root cause:** The Kotlin IR API deprecated `IrPluginContext.referenceClass()` in favor of typed finders.

**Fix:** Replace the `referenceClass(classId)` call with:
- `finderForBuiltins().findClass(classId)` for stdlib/built-in classes
- `finderForSource(fromFile).findClass(classId)` for source-defined classes

This requires understanding which classes are being referenced to choose the right finder. This is the most impactful fix as it involves a compiler plugin API migration.

---

### 5. Deprecated `TypeInfo` constructor & `platformType`

**Warnings:**
```
w: .../TypeData.kt:42:53
   'constructor(type: KClass<*>, reifiedType: Type, kotlinType: KType? = ...): TypeInfo'
   is deprecated. Use constructor without reifiedType parameter.

w: .../TypeData.kt:42:85
   'val KType.platformType: Type' is deprecated. Use KType.javaType instead.

w: .../TypeInfoExt.kt:16:12
   'constructor(type: KClass<*>, reifiedType: KType, kotlinType: KType? = ...): TypeInfo'
   is deprecated. Use constructor without reifiedType parameter.
```

**Source:**
- `ktorfit-lib-core/src/commonMain/kotlin/de/jensklingenberg/ktorfit/converter/TypeData.kt:42`
- `ktorfit-lib-core/src/commonMain/kotlin/de/jensklingenberg/ktorfit/TypeInfoExt.kt:16`

**Root cause:** Ktor deprecated the `TypeInfo` constructor that takes a `reifiedType` parameter, and `KType.platformType` in favor of `KType.javaType`.

**Fix:** Update the `TypeInfo` construction calls to use the new constructor (without `reifiedType`). Replace `platformType` with `javaType`. This is a `commonMain` file so need to verify `javaType` is available in common source set (it may need to be in a JVM-specific expect/actual).

---

### 6. Deprecated `create()` in tests and sandbox

**Warnings (~10 instances):**
```
w: .../BodyTest.kt:42
w: .../BuilderDefaultResponseConverter.kt:73
w: .../ConverterFactoryTest.kt:71, :114
w: .../KtorfitClientTest.kt:55, :82, :126
w: .../JvMMain.kt:60
w: .../LinuxMain.kt:16
```

**Message:** `'fun <T> create(classProvider: ClassProvider<T>? = ...): T' is deprecated.`

**Root cause:** Tests and sandbox code use the deprecated `create()` overload of `Ktorfit`.

**Fix:** Update code to use the non-deprecated `create()` API. Since the deprecation message says it "relies on a compiler plugin", the tests should either use the compiler-plugin-backed variant or use the appropriate alternative API.

---

## Recommended Priority Order

### Phase 1: API migrations
1. **#5 — TypeInfo/platformType deprecation** — Update Ktor API usage
2. **#3 — ExperimentalCompilerApi** — Add dependency or remove opt-in
3. **#6 — Deprecated create() in tests** — Update test API calls

### Phase 2: Deeper changes
4. **#4 — referenceClass deprecation** — Migrate compiler plugin IR API

### No action needed
- **#1 — Kotlin version mismatch** — Resolves with stable Kotlin release
- **#2 — -Xjvm-default** — Upstream Gradle/kapt issue
