package com.github.yamanakahirofumi.siren.markdown

import org.intellij.plugins.markdown.extensions.MarkdownBrowserPreviewExtension
import org.intellij.plugins.markdown.ui.preview.ResourceProvider

class MermaidMarkdownPreviewExtension : MarkdownBrowserPreviewExtension, ResourceProvider {
    override val styles: List<String>
        get() = emptyList()

    override val scripts: List<String>
        get() = listOf(
            "mermaid.min.js",
            "mermaid_markdown_preview.js"
        )

    override val resourceProvider: ResourceProvider
        get() = this

    override fun canProvide(resourceName: String): Boolean =
        resourceName == "mermaid.min.js" || resourceName == "mermaid_markdown_preview.js"

    override fun loadResource(resourceName: String): ResourceProvider.Resource? {
        val resourcePath = if (resourceName.startsWith("/")) resourceName else "/$resourceName"
        val url = javaClass.getResource(resourcePath) ?: return null
        return ResourceProvider.Resource(url.readBytes(), "application/javascript")
    }

    override fun dispose() {}

    override fun compareTo(other: MarkdownBrowserPreviewExtension): Int {
        return 0
    }
}
