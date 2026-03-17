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
        assertTrue(lookupElementStrings!!.contains("sequenceDiagram"))
        assertTrue(lookupElementStrings!!.contains("classDiagram"))
        assertTrue(lookupElementStrings!!.contains("stateDiagram"))
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
}
