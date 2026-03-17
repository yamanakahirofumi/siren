package com.github.yamanakahirofumi.siren.markdown

import org.intellij.plugins.markdown.extensions.MarkdownBrowserPreviewExtension
import org.intellij.plugins.markdown.ui.preview.ResourceProvider
import java.net.URL

class MermaidBrowserPreviewExtension : MarkdownBrowserPreviewExtension, ResourceProvider {
    override fun dispose() {}

    override val priority: MarkdownBrowserPreviewExtension.Priority
        get() = MarkdownBrowserPreviewExtension.Priority.AFTER_ALL

    override val scripts: List<String>
        get() = listOf("mermaid-siren/mermaid.min.js", "mermaid-siren/render-mermaid.js")

    override val resourceProvider: ResourceProvider
        get() = this

    override fun canProvide(resourceName: String): Boolean {
        return resourceName == "mermaid-siren/mermaid.min.js" || resourceName == "mermaid-siren/render-mermaid.js"
    }

    override fun loadResource(resourceName: String): ResourceProvider.Resource? {
        val resourcePath = when (resourceName) {
            "mermaid-siren/mermaid.min.js" -> "/mermaid.min.js"
            "mermaid-siren/render-mermaid.js" -> "/mermaid-siren/render-mermaid.js"
            else -> return null
        }

        val url: URL = javaClass.getResource(resourcePath) ?: return null
        val bytes = url.openStream().use { it.readBytes() }
        return ResourceProvider.Resource(bytes, "application/javascript")
    }
}
