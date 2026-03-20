package com.github.yamanakahirofumi.siren

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class MermaidInputAssistanceTest : BasePlatformTestCase() {

    fun testCompletion() {
        myFixture.configureByText("test.mermaid", "gra<caret>")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertTrue(lookupElementStrings!!.contains("graph"))
    }

    fun testCompletionDiagramTypes() {
        myFixture.configureByText("test.mermaid", "<caret>")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        val expectedTypes = listOf(
            "C4Context", "classDiagram", "erDiagram", "flowchart", "gantt", "gitGraph", "graph", "journey", "mindmap",
            "pie", "requirementDiagram", "sequenceDiagram", "stateDiagram", "stateDiagram-v2", "timeline"
        )
        for (type in expectedTypes) {
            assertTrue("Should contain $type", lookupElementStrings!!.contains(type))
        }
    }

    fun testCompletionKeywords() {
        myFixture.configureByText("test.mermaid", "graph TD\nsub<caret>")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        // In some contexts, lookup might be null if there's only one item and it's auto-inserted.
        if (lookupElementStrings == null) {
            myFixture.checkResult("graph TD\nsubgraph")
        } else {
            assertTrue(lookupElementStrings.contains("subgraph"))
        }
    }

    fun testLineComment() {
        myFixture.configureByText("test.mermaid", "graph TD<caret>")
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
        myFixture.checkResult("%%graph TD")
    }

    fun testUncomment() {
        myFixture.configureByText("test.mermaid", "%%graph TD<caret>")
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
        myFixture.checkResult("graph TD")
    }

    fun testBraceMatching() {
        myFixture.configureByText("test.mermaid", "graph TD\n  A<caret>")
        myFixture.type('(')
        myFixture.checkResult("graph TD\n  A(<caret>)")

        // Remove parens for next test or just continue.
        // If I type '[', and brace matching is on, it might result in A([<caret>]) if it's smart,
        // but here it seems it's just basic.
    }
}
