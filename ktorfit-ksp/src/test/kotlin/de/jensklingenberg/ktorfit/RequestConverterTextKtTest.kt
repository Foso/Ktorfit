package de.jensklingenberg.ktorfit

import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.FunSpec
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.RequestType
import de.jensklingenberg.ktorfit.reqBuilderExtension.addRequestConverterText
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RequestConverterTextKtTest {

    @Test
    fun generateCorrectFunction() {
        val expected = """public fun TestFunction() {
  val test1: com.example.Test = _helper.convertParameterType(test1,test1::class,com.example.Test::class)
}
"""

        val ksType = createKsType("Test","com.example")
        val parameterAnnotations = listOf<ParameterAnnotation>(RequestType(ksType))
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", null), annotations = parameterAnnotations)
        val params = listOf(parameterData)


        val actualSource = FunSpec.builder("TestFunction").addRequestConverterText(params).build().toString()

        assertEquals(expected, actualSource)
    }


}

fun createKsType(name: String, packageName: String): KSType {
    val mockDec = mock<KSClassDeclaration>()

    whenever(mockDec.simpleName).thenAnswer { name }

    val packagemockKSName = object : KSName {
        override fun asString(): String {
            return packageName
        }

        override fun getQualifier(): String {
            return ""
        }

        override fun getShortName(): String {
            return ""
        }

    }
    val qualifiedMockKSName = object : KSName {
        override fun asString(): String {
            return name
        }

        override fun getQualifier(): String {
            return ""
        }

        override fun getShortName(): String {
            return ""
        }

    }
    whenever(mockDec.packageName).thenAnswer { packagemockKSName }
    whenever(mockDec.qualifiedName).thenAnswer { qualifiedMockKSName }

    val ksType = mock<KSType>()
    whenever(ksType.declaration).thenAnswer { mockDec }
    return ksType
}