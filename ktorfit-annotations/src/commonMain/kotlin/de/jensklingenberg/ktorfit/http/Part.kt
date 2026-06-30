package de.jensklingenberg.ktorfit.http

/**
 * String OR List< PartData>
 * ```
 * @Multipart
 * @POST("upload")
 * suspend fun uploadFile(@Part("description") description: String, @Part("description") data: List<PartData>): String
 * ```
 * @param value part name
 * Part parameters type may not be nullable.
 */
@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Part(
    val value: String = ""
)
