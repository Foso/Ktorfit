package de.jensklingenberg.ktorfit.http

/**
 * A function that is annotated with this, will have the header
 * application/x-www-form-urlencoded added to the request.
 * Needed to use @Field @FieldMap
 */
@Target(AnnotationTarget.FUNCTION)
annotation class FormUrlEncoded
