package com.github.yamanakahirofumi.siren.lexer

import com.github.yamanakahirofumi.siren.psi.MermaidTokenTypes
import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class MermaidLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var startOffset: Int = 0
    private var endOffset: Int = 0
    private var currentOffset: Int = 0
    private var tokenType: IElementType? = null
    private var tokenEnd: Int = 0

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.currentOffset = startOffset
        this.tokenEnd = startOffset
        advance()
    }

    override fun getState(): Int = 0

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = currentOffset

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        currentOffset = tokenEnd
        if (currentOffset >= endOffset) {
            tokenType = null
            return
        }

        val remaining = buffer.subSequence(currentOffset, endOffset)

        if (tryHandleWhitespace(remaining)) return
        if (tryHandleComment(remaining)) return
        if (tryHandleBraces(remaining)) return
        if (tryHandleArrows(remaining)) return

        handleKeywordsAndText(remaining)
    }

    private fun tryHandleWhitespace(remaining: CharSequence): Boolean {
        var i = 0
        while (i < remaining.length && remaining[i].isWhitespace()) {
            i++
        }

        if (i > 0) {
            tokenType = com.intellij.psi.TokenType.WHITE_SPACE
            tokenEnd = currentOffset + i
            return true
        }
        return false
    }

    private fun tryHandleComment(remaining: CharSequence): Boolean {
        if (remaining.startsWith("%%")) {
            tokenType = MermaidTokenTypes.COMMENT
            var end = 2
            while (end < remaining.length && remaining[end] != '\n' && remaining[end] != '\r') {
                end++
            }
            tokenEnd = currentOffset + end
            return true
        }
        return false
    }

    private fun tryHandleBraces(remaining: CharSequence): Boolean {
        val type = when (remaining[0]) {
            '(' -> MermaidTokenTypes.LPAREN
            ')' -> MermaidTokenTypes.RPAREN
            '[' -> MermaidTokenTypes.LBRACKET
            ']' -> MermaidTokenTypes.RBRACKET
            '{' -> MermaidTokenTypes.LBRACE
            '}' -> MermaidTokenTypes.RBRACE
            else -> null
        }

        if (type != null) {
            tokenType = type
            tokenEnd = currentOffset + 1
            return true
        }
        return false
    }

    private fun tryHandleArrows(remaining: CharSequence): Boolean {
        for (arrow in ARROWS) {
            if (remaining.startsWith(arrow)) {
                tokenType = MermaidTokenTypes.ARROW
                tokenEnd = currentOffset + arrow.length
                return true
            }
        }
        return false
    }

    private fun handleKeywordsAndText(remaining: CharSequence) {
        var end = 0
        while (end < remaining.length &&
            !remaining[end].isWhitespace() &&
            remaining[end] !in "()[]{}" &&
            !isArrowStart(remaining.subSequence(end, remaining.length))
        ) {
            end++
        }

        if (end == 0 && remaining.isNotEmpty()) {
            end = 1
        }

        val word = remaining.subSequence(0, end).toString()
        tokenType = if (MermaidTokenTypes.KEYWORDS_STR.contains(word)) {
            MermaidTokenTypes.KEYWORD
        } else {
            MermaidTokenTypes.TEXT
        }
        tokenEnd = currentOffset + end
    }

    private fun isArrowStart(remaining: CharSequence): Boolean {
        for (arrow in ARROWS) {
            if (remaining.startsWith(arrow)) return true
        }
        return false
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = endOffset

    companion object {
        private val ARROWS = listOf("-->", "---", "->>", "->", "--x", "-x", "--)", "-)").sortedByDescending { it.length }
    }
}
