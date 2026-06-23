package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import kotlin.reflect.KClass

/**
 * Registry of API factory functions, populated by code embedded directly
 * in each generated `_<Name>Impl.kt` file — avoiding file-name collisions
 * in multi-module projects.
 *
 * Each generated impl file registers its own API class via a module-level
 * property initializer, so registration happens eagerly when the generated
 * class is loaded.
 */
@InternalKtorfitApi
public object KtorfitApiRegistry {
    private val factories: MutableMap<KClass<*>, (Ktorfit) -> Any> = mutableMapOf()

    public fun register(kClass: KClass<*>, factory: (Ktorfit) -> Any) {
        factories[kClass] = factory
    }

    public operator fun get(kClass: KClass<*>): ((Ktorfit) -> Any)? = factories[kClass]
}
