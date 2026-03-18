package com.github.yamanakahirofumi.siren.highlight

import com.github.yamanakahirofumi.siren.lexer.MermaidLexer
import com.github.yamanakahirofumi.siren.psi.MermaidTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType

class MermaidSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        val KEYWORD = TextAttributesKey.createTextAttributesKey("MERMAID_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val COMMENT = TextAttributesKey.createTextAttributesKey("MERMAID_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BRACE = TextAttributesKey.createTextAttributesKey("MERMAID_BRACE", DefaultLanguageHighlighterColors.BRACES)
        val ARROW = TextAttributesKey.createTextAttributesKey("MERMAID_ARROW", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val TEXT = TextAttributesKey.createTextAttributesKey("MERMAID_TEXT", DefaultLanguageHighlighterColors.IDENTIFIER)

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val BRACE_KEYS = arrayOf(BRACE)
        private val ARROW_KEYS = arrayOf(ARROW)
        private val TEXT_KEYS = arrayOf(TEXT)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = MermaidLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return when (tokenType) {
            MermaidTokenTypes.KEYWORD -> KEYWORD_KEYS
            MermaidTokenTypes.COMMENT -> COMMENT_KEYS
            MermaidTokenTypes.LPAREN, MermaidTokenTypes.RPAREN,
            MermaidTokenTypes.LBRACKET, MermaidTokenTypes.RBRACKET,
            MermaidTokenTypes.LBRACE, MermaidTokenTypes.RBRACE -> BRACE_KEYS
            MermaidTokenTypes.ARROW -> ARROW_KEYS
            MermaidTokenTypes.TEXT -> TEXT_KEYS
            else -> EMPTY_KEYS
        }
    }
}

class MermaidSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return MermaidSyntaxHighlighter()
    }
}
