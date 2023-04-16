import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.request.RequestConverter
import de.jensklingenberg.ktorfit.internal.Client
import de.jensklingenberg.ktorfit.internal.InternalKtorfitApi
import de.jensklingenberg.ktorfit.internal.KtorfitClient
import org.junit.Assert
import org.junit.Test
import kotlin.reflect.KClass

private class TestStringToIntRequestConverter : RequestConverter {
    override fun supportedType(parameterType: KClass<*>, requestType: KClass<*>): Boolean {
        val parameterIsString = parameterType == String::class
        val requestIsInt = requestType == Int::class
        return parameterIsString && requestIsInt
    }

    override fun convert(data: Any): Any {
        return (data as String).toInt()
    }
}


@OptIn(InternalKtorfitApi::class)
class RequestConverterTest {

    @Test
    fun testRequestConverter() {

        val ktorfit =
            Ktorfit.Builder().baseUrl("http://www.test.de/").requestConverter(TestStringToIntRequestConverter()).build()

        val converted = (KtorfitClient(ktorfit) as Client).convertParameterType("4", String::class, Int::class)
        Assert.assertEquals(4, converted)

    }

    @Test
    fun throwExceptionWhenRequestConverterMissing() {
        try {

            val ktorfit = Ktorfit.Builder().baseUrl("http://www.test.de/").build()

            val converted = (KtorfitClient(ktorfit) as Client).convertParameterType("4", String::class, Int::class)
            Assert.assertEquals(4, converted)

        } catch (ex: Exception) {
            Assert.assertTrue(ex is IllegalArgumentException)
            Assert.assertEquals(true, ex.message!!.contains("No RequestConverter found to convert "))
        }
    }

}


