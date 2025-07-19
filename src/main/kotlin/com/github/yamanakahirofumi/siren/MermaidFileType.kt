package com.github.yamanakahirofumi.siren

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.LanguageFileType

class MermaidFileType : LanguageFileType(MermaidLanguage.INSTANCE) {
    companion object {
        val INSTANCE = MermaidFileType()
    }

    override fun getName() = "Mermaid"
    override fun getDescription() = "Mermaid diagram file"
    override fun getDefaultExtension() = "mermaid"
    override fun getIcon() = AllIcons.FileTypes.Text
}