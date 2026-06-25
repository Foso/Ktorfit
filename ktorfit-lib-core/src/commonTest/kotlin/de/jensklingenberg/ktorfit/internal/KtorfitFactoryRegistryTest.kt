@file:Suppress("unused")

package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.TestEngine
import de.jensklingenberg.ktorfit.optins.KtorfitFactoryRegistry
import io.ktor.client.request.HttpRequestData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(InternalKtorfitApi::class)
class KtorfitFactoryRegistryTest {
    // Each test uses its own interface types to avoid
    // polluting the global singleton state across tests.

    interface ApiA

    interface ApiB

    interface ApiC

    interface UnregisteredApi

    @Test
    fun `register single factory and retrieve it`() {
        val ktorfit = buildKtorfit()
        KtorfitFactoryRegistry.register(
            ApiA::class,
            TestClassProvider { ImplA(it) },
        )

        val factory = KtorfitFactoryRegistry[ApiA::class]

        assertNotNull(factory)
        assertTrue(factory.create(ktorfit) is ImplA)
    }

    @Test
    fun `get returns null for unregistered class`() {
        val factory = KtorfitFactoryRegistry[UnregisteredApi::class]
        assertNull(factory)
    }

    @Test
    fun `register overwrites previous factory for same class`() {
        val ktorfit = buildKtorfit()
        KtorfitFactoryRegistry.register(
            ApiB::class,
            TestClassProvider { ImplB1(it) },
        )
        KtorfitFactoryRegistry.register(
            ApiB::class,
            TestClassProvider { ImplB2(it) },
        )

        val factory = KtorfitFactoryRegistry[ApiB::class]
        assertNotNull(factory)
        assertTrue(factory.create(ktorfit) is ImplB2)
    }

    @Test
    fun `multiple classes can be registered independently`() {
        val ktorfit = buildKtorfit()

        KtorfitFactoryRegistry.register(
            ApiA::class,
            TestClassProvider { ImplA(it) },
        )
        KtorfitFactoryRegistry.register(
            ApiC::class,
            TestClassProvider { ImplC(it) },
        )

        val factoryA = KtorfitFactoryRegistry[ApiA::class]
        val factoryC = KtorfitFactoryRegistry[ApiC::class]

        assertNotNull(factoryA)
        assertNotNull(factoryC)
        assertTrue(factoryA.create(ktorfit) is ImplA)
        assertTrue(factoryC.create(ktorfit) is ImplC)
    }

    @Test
    fun `factory receives the ktorfit instance passed at retrieval`() {
        val expected = buildKtorfit()
        var received: Ktorfit? = null

        KtorfitFactoryRegistry.register(
            ApiA::class,
            TestClassProvider { kf ->
                received = kf
                ImplA(kf)
            },
        )

        KtorfitFactoryRegistry[ApiA::class]?.create(expected)

        assertEquals(expected, received)
    }

    @Test
    fun `after register factory creates distinct instances per invocation`() {
        val ktorfit = buildKtorfit()
        KtorfitFactoryRegistry.register(
            ApiA::class,
            TestClassProvider { ImplA(it) },
        )

        val factory = KtorfitFactoryRegistry[ApiA::class]
        assertNotNull(factory)

        val instance1 = factory.create(ktorfit)
        val instance2 = factory.create(ktorfit)
        assertTrue(instance1 !== instance2)
    }

    private fun buildKtorfit(): Ktorfit {
        val engine =
            object : TestEngine() {
                override fun getRequestData(data: HttpRequestData) {
                }
            }
        return Ktorfit
            .Builder()
            .httpClient(engine)
            .baseUrl("http://test.de/")
            .build()
    }

    private fun interface TestClassProvider<T> : ClassProvider<T>

    private class ImplA(
        ktorfit: Ktorfit,
    ) : ApiA

    private class ImplB1(
        ktorfit: Ktorfit,
    ) : ApiB

    private class ImplB2(
        ktorfit: Ktorfit,
    ) : ApiB

    private class ImplC(
        ktorfit: Ktorfit,
    ) : ApiC
}
