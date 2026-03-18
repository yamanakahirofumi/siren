package com.github.yamanakahirofumi.siren.editor

import com.github.yamanakahirofumi.siren.psi.MermaidTokenTypes
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class MermaidBraceMatcher : PairedBraceMatcher {
    private val PAIRS = arrayOf(
        BracePair(MermaidTokenTypes.LPAREN, MermaidTokenTypes.RPAREN, false),
        BracePair(MermaidTokenTypes.LBRACKET, MermaidTokenTypes.RBRACKET, false),
        BracePair(MermaidTokenTypes.LBRACE, MermaidTokenTypes.RBRACE, false)
    )

    override fun getPairs(): Array<BracePair> = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile?, lbraceOffset: Int): Int = lbraceOffset
}
