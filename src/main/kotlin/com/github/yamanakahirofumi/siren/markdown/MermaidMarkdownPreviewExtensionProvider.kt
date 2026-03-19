package com.github.yamanakahirofumi.siren.markdown

import org.intellij.plugins.markdown.extensions.MarkdownBrowserPreviewExtension
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanel

class MermaidMarkdownPreviewExtensionProvider : MarkdownBrowserPreviewExtension.Provider {
    override fun createBrowserExtension(panel: MarkdownHtmlPanel): MarkdownBrowserPreviewExtension {
        return MermaidMarkdownPreviewExtension()
    }
}
