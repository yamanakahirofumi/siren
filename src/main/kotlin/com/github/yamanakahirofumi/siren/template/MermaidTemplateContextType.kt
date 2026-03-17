package com.github.yamanakahirofumi.siren.template

import com.github.yamanakahirofumi.siren.MermaidLanguage
import com.intellij.codeInsight.template.TemplateContextType

class MermaidTemplateContextType : TemplateContextType("MERMAID", "Mermaid") {
    override fun isInContext(templateActionContext: com.intellij.codeInsight.template.TemplateActionContext): Boolean {
        return templateActionContext.file.language is MermaidLanguage
    }
}
