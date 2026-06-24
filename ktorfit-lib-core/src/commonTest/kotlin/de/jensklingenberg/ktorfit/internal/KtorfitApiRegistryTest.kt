package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.TestEngine
import io.ktor.client.request.HttpRequestData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(InternalKtorfitApi::class)
class KtorfitApiRegistryTest {

    // Each test uses its own interface types to avoid
    // polluting the global singleton state across tests.

    interface ApiA
    interface ApiB
    interface ApiC
    interface UnregisteredApi

    @Test
    fun `register single factory and retrieve it`() {
        val ktorfit = buildKtorfit()
        KtorfitApiRegistry.register(ApiA::class) { ImplA(it) }

        val factory = KtorfitApiRegistry[ApiA::class]

        assertNotNull(factory)
        assertTrue(factory(ktorfit) is ImplA)
    }

    @Test
    fun `get returns null for unregistered class`() {
        val factory = KtorfitApiRegistry[UnregisteredApi::class]
        assertNull(factory)
    }

    @Test
    fun `register overwrites previous factory for same class`() {
        val ktorfit = buildKtorfit()
        KtorfitApiRegistry.register(ApiB::class) { ImplB1(it) }
        KtorfitApiRegistry.register(ApiB::class) { ImplB2(it) }

        val factory = KtorfitApiRegistry[ApiB::class]
        assertNotNull(factory)
        assertTrue(factory(ktorfit) is ImplB2)
    }

    @Test
    fun `multiple classes can be registered independently`() {
        val ktorfit = buildKtorfit()

        KtorfitApiRegistry.register(ApiA::class) { ImplA(it) }
        KtorfitApiRegistry.register(ApiC::class) { ImplC(it) }

        val factoryA = KtorfitApiRegistry[ApiA::class]
        val factoryC = KtorfitApiRegistry[ApiC::class]

        assertNotNull(factoryA)
        assertNotNull(factoryC)
        assertTrue(factoryA(ktorfit) is ImplA)
        assertTrue(factoryC(ktorfit) is ImplC)
    }

    @Test
    fun `factory receives the ktorfit instance passed at retrieval`() {
        val expected = buildKtorfit()
        var received: Ktorfit? = null

        KtorfitApiRegistry.register(ApiA::class) { kf ->
            received = kf
            ImplA(kf)
        }

        KtorfitApiRegistry[ApiA::class]?.invoke(expected)

        assertEquals(expected, received)
    }

    @Test
    fun `after register, factory creates distinct instances per invocation`() {
        val ktorfit = buildKtorfit()
        KtorfitApiRegistry.register(ApiA::class) { ImplA(it) }

        val factory = KtorfitApiRegistry[ApiA::class]
        assertNotNull(factory)

        val instance1 = factory(ktorfit)
        val instance2 = factory(ktorfit)
        assertTrue(instance1 !== instance2)
    }

    private fun buildKtorfit(): Ktorfit {
        val engine =
            object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }
        return Ktorfit.Builder()
            .httpClient(engine)
            .baseUrl("http://test.de/")
            .build()
    }

    private class ImplA(ktorfit: Ktorfit) : ApiA

    private class ImplB1(ktorfit: Ktorfit) : ApiB

    private class ImplB2(ktorfit: Ktorfit) : ApiB

    private class ImplC(ktorfit: Ktorfit) : ApiC
}
