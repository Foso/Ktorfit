package de.jensklingenberg.ktorfit.http


/**
 *


&#64;GET()

suspend fun request(@Url url: String): List< Comment>

 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Url