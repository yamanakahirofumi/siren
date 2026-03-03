package com.github.yamanakahirofumi.siren.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class MermaidCompletionContributor : CompletionContributor() {

    private val diagramTypes = listOf(
        "graph",
        "sequenceDiagram",
        "classDiagram",
        "stateDiagram",
        "gantt",
        "pie",
        "erDiagram",
        "flowchart"
    )

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
                    diagramTypes.forEach {
                        result.addElement(LookupElementBuilder.create(it))
                    }
                }
            }
        )
    }
}
