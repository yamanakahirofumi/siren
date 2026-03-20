package com.github.yamanakahirofumi.siren

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class MermaidBoundaryTest : BasePlatformTestCase() {
    fun testEmptyFile() {
        myFixture.configureByText("empty.mermaid", "")
        // If no exception is thrown and file is recognized, it's a success
        assertEquals(MermaidFileType.INSTANCE, myFixture.file.fileType)
    }

    fun testOnlyComments() {
        myFixture.configureByText("comments.mermaid", "%% This is a comment\n%% Another comment")
        assertEquals(MermaidFileType.INSTANCE, myFixture.file.fileType)
    }

    fun testSpecialCharacters() {
        myFixture.configureByText("special.mermaid", "graph TD\n  A[こんにちは] --> B[World! @#\$%^&*()]")
        // Lexer and parser should handle this as TEXT
        assertEquals(MermaidFileType.INSTANCE, myFixture.file.fileType)
    }
}
