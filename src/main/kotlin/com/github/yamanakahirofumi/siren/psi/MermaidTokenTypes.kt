package com.github.yamanakahirofumi.siren.psi

import com.github.yamanakahirofumi.siren.MermaidLanguage
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

interface MermaidTokenTypes {
    companion object {
        val FILE = IFileElementType(MermaidLanguage.INSTANCE)

        val COMMENT = IElementType("MERMAID_COMMENT", MermaidLanguage.INSTANCE)
        val KEYWORD = IElementType("MERMAID_KEYWORD", MermaidLanguage.INSTANCE)
        val TEXT = IElementType("MERMAID_TEXT", MermaidLanguage.INSTANCE)

        val LPAREN = IElementType("MERMAID_LPAREN", MermaidLanguage.INSTANCE)
        val RPAREN = IElementType("MERMAID_RPAREN", MermaidLanguage.INSTANCE)
        val LBRACKET = IElementType("MERMAID_LBRACKET", MermaidLanguage.INSTANCE)
        val RBRACKET = IElementType("MERMAID_RBRACKET", MermaidLanguage.INSTANCE)
        val LBRACE = IElementType("MERMAID_LBRACE", MermaidLanguage.INSTANCE)
        val RBRACE = IElementType("MERMAID_RBRACE", MermaidLanguage.INSTANCE)

        val ARROW = IElementType("MERMAID_ARROW", MermaidLanguage.INSTANCE)

        val COMMENTS = TokenSet.create(COMMENT)

        val KEYWORDS_STR = setOf(
            // Diagram types
            "C4Context", "classDiagram", "erDiagram", "flowchart", "gantt", "gitGraph", "graph", "journey", "mindmap",
            "pie", "requirementDiagram", "sequenceDiagram", "stateDiagram", "stateDiagram-v2", "timeline",
            "zenuml", "sankey-beta", "xychart-beta", "block-beta", "packet", "kanban", "architecture-beta", "radar-beta", "treemap-beta", "venn-beta",

            // General keywords
            "activate", "alt", "and", "as", "actor", "autonumber", "break", "callback", "class", "click", "critical",
            "deactivate", "else", "end", "left", "link", "linkStyle", "loop", "note", "opt", "option", "over", "par",
            "participant", "rect", "right", "style", "subgraph", "title"
        )
    }
}
