package de.jensklingenberg

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class TreeVis(private val oldFile: KtFile, messageCollector: MessageCollector) : KtTreeVisitorVoid() {
    private val patches = mutableListOf<Pair<PsiElement, String>>()

    fun buildOutput(): String? {
        oldFile.accept(this)

        return if (patches.isEmpty()) {
            null
        } else {
            applyPatches(oldFile.text, patches)
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

    }

    override fun visitExpression(expression: KtExpression) {
        super.visitExpression(expression)
        if (expression.text.contains("create<")) {
            expression
          //  patches.add(Pair(expression, "jvmKtorfit.create<JsonPlaceHolderApi>(_JsonPlaceHolderApiImpl())"))
        }

    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)
        if (expression.text.contains("jvmKtorfit.create<")) {
            patches.add(Pair(expression, "jvmKtorfit.create<JsonPlaceHolderApi>(com.example.api._JsonPlaceHolderApiImpl())"))
        }
    }

    override fun visitParameterList(list: KtParameterList) {
        super.visitParameterList(list)
    }

    override fun visitTypeArgumentList(typeArgumentList: KtTypeArgumentList) {
        super.visitTypeArgumentList(typeArgumentList)
    }

    override fun visitArgument(argument: KtValueArgument) {
        super.visitArgument(argument)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
    }

    private fun applyPatches(oldText: String, patches: List<Pair<PsiElement, String>>): String {
        val sortedPatches: List<Pair<PsiElement, String>> =
            patches.sortedBy { it.first.startOffset }

        // Check no patch intersect
        var previousPatchEndOffset = -1
        for (patch in sortedPatches) {
            if (patch.first.startOffset <= previousPatchEndOffset) {
                throw IllegalArgumentException("Cannot apply patches. Patches intersect.")
            }
            previousPatchEndOffset = patch.first.endOffset
        }

        // Apply patch in reverse order, so each patch won't affect next patch's offset.
        var newText = oldText
        for ((element, replacement) in sortedPatches.reversed()) {
            newText = newText.substring(0, element.startOffset) + replacement + newText.substring(element.endOffset)
        }
        return newText
    }
}