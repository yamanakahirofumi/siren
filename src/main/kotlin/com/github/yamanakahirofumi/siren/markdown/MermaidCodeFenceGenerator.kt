package com.github.yamanakahirofumi.siren.markdown

import org.intellij.plugins.markdown.extensions.CodeFenceGeneratingProvider
import org.jetbrains.annotations.NotNull

import java.util.UUID

import org.intellij.markdown.ast.ASTNode

class MermaidCodeFenceGenerator : CodeFenceGeneratingProvider {
    private val mermaidScript: String by lazy {
        javaClass.getResource("/mermaid.min.js")?.readText() ?: ""
    }

    override fun generateHtml(language: String, raw: String, node: ASTNode): String {
        val diagramId = "mermaid-${UUID.randomUUID()}"
        return """
            <div class="mermaid" id="$diagramId">
                $raw
            </div>
            <script>
                ${mermaidScript}
                mermaid.run({
                    nodes: [document.getElementById('$diagramId')]
                });
            </script>
        """.trimIndent()
    }

    override fun isApplicable(language: String): Boolean {
        return language == "mermaid" || language == "mmd"
    }
}
