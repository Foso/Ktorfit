package de.jensklingenberg.ktorfit.core

/**
 * Indicates that the annotated interface should not be delegated in the generated implementation.
 *
 * When an interface is annotated with @NoDelegation, the generated implementation will not use
 * Kotlin delegation for this interface. This is useful when you want to manually implement the
 * methods of the interface or when delegation is not desired for other reasons.
 *
 * Example:
 * interface MyApi : OtherApi, @NoDelegation BaseApi
 */
@Target(AnnotationTarget.TYPE)
annotation class NoDelegation
