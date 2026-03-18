package com.github.yamanakahirofumi.siren.completion

import com.github.yamanakahirofumi.siren.psi.MermaidTokenTypes
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class MermaidCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    for (keyword in MermaidTokenTypes.KEYWORDS_STR) {
                        result.addElement(LookupElementBuilder.create(keyword))
                    }
                }
            }
        )
    }
}
