package com.github.yamanakahirofumi.siren.psi

import com.github.yamanakahirofumi.siren.MermaidLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class MermaidFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, MermaidLanguage.INSTANCE) {
    override fun getFileType(): FileType = com.github.yamanakahirofumi.siren.MermaidFileType.INSTANCE
    override fun toString(): String = "Mermaid File"
}
