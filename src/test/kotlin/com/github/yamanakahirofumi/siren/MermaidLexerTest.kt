package com.github.yamanakahirofumi.siren

import com.github.yamanakahirofumi.siren.lexer.MermaidLexer
import com.github.yamanakahirofumi.siren.psi.MermaidTokenTypes
import com.intellij.psi.TokenType
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class MermaidLexerTest : BasePlatformTestCase() {
    fun testLexer() {
        val lexer = MermaidLexer()
        val text = "graph TD\n  %% comment\n  A --> B"
        lexer.start(text)

        assertEquals(MermaidTokenTypes.KEYWORD, lexer.tokenType)
        assertEquals("graph", lexer.tokenText)
        lexer.advance()

        assertEquals(TokenType.WHITE_SPACE, lexer.tokenType)
        lexer.advance()

        assertEquals(MermaidTokenTypes.TEXT, lexer.tokenType)
        assertEquals("TD", lexer.tokenText)
        lexer.advance()

        assertEquals(TokenType.WHITE_SPACE, lexer.tokenType)
        lexer.advance()

        assertEquals(MermaidTokenTypes.COMMENT, lexer.tokenType)
        assertEquals("%% comment", lexer.tokenText)
        lexer.advance()

        assertEquals(TokenType.WHITE_SPACE, lexer.tokenType)
        lexer.advance()

        assertEquals(MermaidTokenTypes.TEXT, lexer.tokenType)
        assertEquals("A", lexer.tokenText)
        lexer.advance()

        assertEquals(TokenType.WHITE_SPACE, lexer.tokenType)
        lexer.advance()

        assertEquals(MermaidTokenTypes.ARROW, lexer.tokenType)
        assertEquals("-->", lexer.tokenText)
        lexer.advance()

        assertEquals(TokenType.WHITE_SPACE, lexer.tokenType)
        lexer.advance()

        assertEquals(MermaidTokenTypes.TEXT, lexer.tokenType)
        assertEquals("B", lexer.tokenText)
        lexer.advance()

        assertNull(lexer.tokenType)
    }

    fun testArrows() {
        val arrows = listOf("-->", "---", "->>", "->", "--x", "-x", "--)", "-)")
        for (arrow in arrows) {
            val lexer = MermaidLexer()
            lexer.start(arrow)
            assertEquals("Testing arrow: $arrow", MermaidTokenTypes.ARROW, lexer.tokenType)
            assertEquals("Testing arrow text: $arrow", arrow, lexer.tokenText)
        }
    }

    fun testBraces() {
        val braces = mapOf(
            "(" to MermaidTokenTypes.LPAREN,
            ")" to MermaidTokenTypes.RPAREN,
            "[" to MermaidTokenTypes.LBRACKET,
            "]" to MermaidTokenTypes.RBRACKET,
            "{" to MermaidTokenTypes.LBRACE,
            "}" to MermaidTokenTypes.RBRACE
        )
        for ((text, type) in braces) {
            val lexer = MermaidLexer()
            lexer.start(text)
            assertEquals("Testing brace: $text", type, lexer.tokenType)
            assertEquals("Testing brace text: $text", text, lexer.tokenText)
        }
    }

    fun testLexerEdgeCases() {
        val lexer = MermaidLexer()

        // Arrow without spaces
        lexer.start("A-->B")
        assertEquals(MermaidTokenTypes.TEXT, lexer.tokenType)
        assertEquals("A", lexer.tokenText)
        lexer.advance()
        assertEquals(MermaidTokenTypes.ARROW, lexer.tokenType)
        assertEquals("-->", lexer.tokenText)
        lexer.advance()
        assertEquals(MermaidTokenTypes.TEXT, lexer.tokenType)
        assertEquals("B", lexer.tokenText)

        // Arrow at the end
        lexer.start("A-->")
        lexer.advance()
        assertEquals(MermaidTokenTypes.ARROW, lexer.tokenType)
        assertEquals("-->", lexer.tokenText)
        lexer.advance()
        assertNull(lexer.tokenType)

        // Keyword as part of identifier
        lexer.start("subgraphing")
        assertEquals(MermaidTokenTypes.TEXT, lexer.tokenType)
        assertEquals("subgraphing", lexer.tokenText)

        // Multiple arrows back to back (unlikely but good for boundary)
        lexer.start("----->")
        assertEquals(MermaidTokenTypes.ARROW, lexer.tokenType)
        assertEquals("---", lexer.tokenText)
        lexer.advance()
        assertEquals(MermaidTokenTypes.ARROW, lexer.tokenType)
        assertEquals("-->", lexer.tokenText)
    }
}
