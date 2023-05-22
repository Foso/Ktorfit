package de.jensklingenberg.ktorfit

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FunSpec
import de.jensklingenberg.ktorfit.model.ParameterData
import de.jensklingenberg.ktorfit.model.ReturnTypeData
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation
import de.jensklingenberg.ktorfit.model.annotations.RequestType
import de.jensklingenberg.ktorfit.requestData.addRequestConverterText
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RequestConverterTextKtTest {

    @Test
    fun generateCorrectFunction() {
        val expected = """public fun TestFunction(): kotlin.Unit {
  val test1: com.example.Test = ktorfitClient.convertParameterType(test1,test1::class,com.example.Test::class)
}
"""

        val mockDec = mock<KSClassDeclaration>()
        val packagemockKSName = object : KSName {
            override fun asString(): String {
                return "com.example"
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
                return "com.example.Test"
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
        val parameterAnnotations = listOf<ParameterAnnotation>(RequestType(ksType))
        val parameterData =
            ParameterData("test1", ReturnTypeData("String", "kotlin.String", null), annotations = parameterAnnotations)
        val params = listOf(parameterData)


        val actualSource = FunSpec.builder("TestFunction").addRequestConverterText(params).build().toString()

        Assert.assertEquals(expected, actualSource)
    }
}