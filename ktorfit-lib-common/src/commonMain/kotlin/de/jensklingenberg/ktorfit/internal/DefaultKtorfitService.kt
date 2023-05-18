package de.jensklingenberg.ktorfit.internal

/**
 * This will be used as default parameter for [Ktorfit.create].
 * When this class is used at runtime, it means that the compiler plugin
 * did not replace the default parameter with the right class
 */
internal class DefaultKtorfitService : KtorfitService {
    override lateinit var ktorfitClient: Client
}