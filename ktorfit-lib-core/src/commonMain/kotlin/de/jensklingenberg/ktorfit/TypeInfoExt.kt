package de.jensklingenberg.ktorfit

import io.ktor.util.reflect.*
import kotlin.reflect.KClass

/**
 * This will return the upper bound type.
 *
 * Example: Response<String> will return String as TypeInfo with upperBoundType(0)
 */
public fun TypeInfo.upperBoundType(index: Int = 0): TypeInfo? {

    val parentType = this.kotlinType ?: return null
    val modelKTypeProjection = if (parentType.arguments.isNotEmpty()) parentType.arguments[index] else return null
    val modelKType = modelKTypeProjection.type ?: return null
    val modelClass = (modelKType.classifier as? KClass<*>?) ?: return null
    return TypeInfo(modelClass, modelKType.platformType, modelKType)
}