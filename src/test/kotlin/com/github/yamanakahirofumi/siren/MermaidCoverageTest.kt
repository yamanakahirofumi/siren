package com.github.yamanakahirofumi.siren

import com.github.yamanakahirofumi.siren.editor.MermaidBraceMatcher
import com.github.yamanakahirofumi.siren.editor.MermaidCommenter
import com.github.yamanakahirofumi.siren.editor.MermaidEditorProvider
import com.github.yamanakahirofumi.siren.highlight.MermaidSyntaxHighlighter
import com.github.yamanakahirofumi.siren.highlight.MermaidSyntaxHighlighterFactory
import com.github.yamanakahirofumi.siren.markdown.MermaidMarkdownPreviewExtension
import com.github.yamanakahirofumi.siren.markdown.MermaidMarkdownPreviewExtensionProvider
import com.github.yamanakahirofumi.siren.psi.MermaidTokenTypes
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanel
import org.mockito.Mockito.mock

class MermaidCoverageTest : BasePlatformTestCase() {

    fun testEditorProvider() {
        val provider = MermaidEditorProvider()
        val mermaidFile = myFixture.configureByText("test.mermaid", "graph TD").virtualFile
        val mmdFile = myFixture.configureByText("test.mmd", "graph TD").virtualFile
        val txtFile = myFixture.configureByText("test.txt", "some text").virtualFile

        assertTrue(provider.accept(project, mermaidFile))
        assertTrue(provider.accept(project, mmdFile))
        assertFalse(provider.accept(project, txtFile))

        assertEquals("MermaidPreview", provider.editorTypeId)
        assertNotNull(provider.policy)
    }

    fun testMarkdownExtension() {
        val extension = MermaidMarkdownPreviewExtension()
        assertTrue(extension.canProvide("mermaid.min.js"))
        assertTrue(extension.canProvide("mermaid_markdown_preview.js"))
        assertFalse(extension.canProvide("unknown.js"))

        assertNotNull(extension.loadResource("mermaid.min.js"))
        assertNotNull(extension.loadResource("/mermaid.min.js"))
        assertNull(extension.loadResource("nonexistent.js"))

        assertEquals(0, extension.compareTo(extension))
        assertTrue(extension.styles.isEmpty())
        assertEquals(2, extension.scripts.size)
        assertEquals(extension, extension.resourceProvider)

        extension.dispose()
    }

    fun testMarkdownExtensionProvider() {
        val provider = MermaidMarkdownPreviewExtensionProvider()
        val panel = mock(MarkdownHtmlPanel::class.java)
        val extension = provider.createBrowserExtension(panel)
        assertNotNull(extension)
        assertTrue(extension is MermaidMarkdownPreviewExtension)
    }

    fun testMyBundle() {
        assertNotNull(MyBundle.message("shuffle"))
        assertNotNull(MyBundle.messagePointer("shuffle"))
    }

    fun testSyntaxHighlighter() {
        val highlighter = MermaidSyntaxHighlighter()
        assertNotNull(highlighter.highlightingLexer)

        assertEquals(1, highlighter.getTokenHighlights(MermaidTokenTypes.KEYWORD).size)
        assertEquals(1, highlighter.getTokenHighlights(MermaidTokenTypes.COMMENT).size)
        assertEquals(1, highlighter.getTokenHighlights(MermaidTokenTypes.ARROW).size)
        assertEquals(1, highlighter.getTokenHighlights(MermaidTokenTypes.TEXT).size)
        assertEquals(1, highlighter.getTokenHighlights(MermaidTokenTypes.LPAREN).size)
        assertEquals(0, highlighter.getTokenHighlights(com.intellij.psi.TokenType.BAD_CHARACTER).size)

        val factory = MermaidSyntaxHighlighterFactory()
        assertNotNull(factory.getSyntaxHighlighter(project, null))
    }

    fun testCommenter() {
        val commenter = MermaidCommenter()
        assertEquals("%%", commenter.lineCommentPrefix)
        assertNull(commenter.blockCommentPrefix)
        assertNull(commenter.blockCommentSuffix)
        assertNull(commenter.commentedBlockCommentPrefix)
        assertNull(commenter.commentedBlockCommentSuffix)
    }

    fun testBraceMatcher() {
        val matcher = MermaidBraceMatcher()
        assertEquals(3, matcher.pairs.size)
        assertTrue(matcher.isPairedBracesAllowedBeforeType(MermaidTokenTypes.LPAREN, null))
        assertEquals(10, matcher.getCodeConstructStart(null, 10))
    }
}
