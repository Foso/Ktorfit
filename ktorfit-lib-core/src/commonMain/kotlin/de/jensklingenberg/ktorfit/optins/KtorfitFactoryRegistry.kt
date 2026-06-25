package de.jensklingenberg.ktorfit.optins

import de.jensklingenberg.ktorfit.internal.ClassProvider
import de.jensklingenberg.ktorfit.internal.InternalKtorfitApi
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
public object KtorfitFactoryRegistry {
    private val factories: MutableMap<KClass<*>, ClassProvider<out Any>> = mutableMapOf()

    public fun register(
        kClass: KClass<*>,
        factory: ClassProvider<out Any>,
    ) {
        factories[kClass] = factory
    }

    @Suppress("UNCHECKED_CAST")
    public operator fun <T : Any> get(kClass: KClass<T>): ClassProvider<T>? = factories[kClass] as? ClassProvider<T>
}
