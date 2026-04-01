package com.github.yamanakahirofumi.siren

import com.intellij.codeInsight.template.impl.TemplateSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class MermaidLiveTemplateTest : BasePlatformTestCase() {
    fun testFlowTemplate() {
        doTest("flow", """
            graph TD
                A[Square Rect] --> B((Circle))
                A --> C(Round Rect)
                B --> D{Rhombus}
                C --> D
        """.trimIndent())
    }

    fun testSeqTemplate() {
        doTest("seq", """
            sequenceDiagram
                participant Alice
                participant Bob
                Alice->>Bob: Hello Bob, how are you?
                Bob-->>Alice: Jolly good!
        """.trimIndent())
    }

    fun testClassTemplate() {
        doTest("class", """
            classDiagram
                Class01 <|-- dev.Class02
                Class03 *-- Class04
                Class05 o-- Class06
        """.trimIndent())
    }

    private fun doTest(templateName: String, expectedResult: String) {
        myFixture.configureByText("test.mermaid", "<caret>")
        val template = TemplateSettings.getInstance().getTemplate(templateName, "Mermaid")
        assertNotNull("Template $templateName not found", template)

        myFixture.type(templateName + "\t")
        println("expectedResult: \n$expectedResult")
        myFixture.checkResult(expectedResult)
    }
}
