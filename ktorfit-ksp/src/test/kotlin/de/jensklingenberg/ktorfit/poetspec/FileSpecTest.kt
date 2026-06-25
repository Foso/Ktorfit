package de.jensklingenberg.ktorfit.poetspec

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import de.jensklingenberg.ktorfit.model.ClassData
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock

class FileSpecTest {
    @Test
    fun `when hasExpectDeclaration is true, generated function has actual modifier`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, hasExpectDeclaration = true)
        val generatedCode = fileSpec.toString()

        assertTrue(generatedCode.contains("actual fun Ktorfit.createTestService()"))
    }

    @Test
    fun `when hasExpectDeclaration is false, generated function does NOT have actual modifier`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, hasExpectDeclaration = false)
        val generatedCode = fileSpec.toString()

        assertTrue(generatedCode.contains("fun Ktorfit.createTestService()"))
        assertFalse(generatedCode.contains("actual fun Ktorfit.createTestService()"))
    }

    @Test
    fun `generated file contains impl class`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, hasExpectDeclaration = false)
        val generatedCode = fileSpec.toString()

        assertTrue(generatedCode.contains("class _TestServiceImpl"))
    }

    @Test
    fun `generated file contains create extension function`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, hasExpectDeclaration = false)
        val generatedCode = fileSpec.toString()

        assertTrue(generatedCode.contains("fun Ktorfit.createTestService()"))
    }

    @Test
    fun `generated file does NOT contain provider class`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, hasExpectDeclaration = false)
        val generatedCode = fileSpec.toString()

        assertFalse(generatedCode.contains("Provider"))
    }

    @Test
    fun `generated file does NOT contain factory registry import`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, hasExpectDeclaration = true)
        val generatedCode = fileSpec.toString()

        assertFalse(generatedCode.contains("KtorfitFactoryRegistry"))
    }

    @Test
    fun `generated file does NOT contain registration property`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, hasExpectDeclaration = true)
        val generatedCode = fileSpec.toString()

        assertFalse(generatedCode.contains("__ktorfit_registration"))
        assertFalse(generatedCode.contains("KtorfitFactoryRegistry.register"))
    }

    private fun createTestClassData(): ClassData {
        val mockFile = mock<KSFile>()
        return ClassData(
            name = "TestService",
            packageName = "com.example.api",
            functions = emptyList(),
            imports = emptySet(),
            superClasses = emptyList(),
            properties = emptyList(),
            modifiers = listOf(KModifier.PUBLIC),
            ksFile = mockFile,
            annotations = emptyList(),
        )
    }
}
