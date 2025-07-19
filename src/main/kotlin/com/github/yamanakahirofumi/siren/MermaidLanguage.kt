package com.github.yamanakahirofumi.siren

import com.intellij.lang.Language

class MermaidLanguage private constructor() : Language("Mermaid") {
    companion object {
        val INSTANCE = MermaidLanguage()
    }
}