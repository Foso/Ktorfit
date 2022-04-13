package de.jensklingenberg.ktorfit.http

/**
 * sTRING OR List< PartData>
 *
 * @param encoded
 *  * <p>Part parameters type may not be nullable.
 *  @Multipart
@POST("upload")
suspend fun uploadFile(@Part("description") description: String, @Part("description") data: List<PartData>): String
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Part(val value: String = "")