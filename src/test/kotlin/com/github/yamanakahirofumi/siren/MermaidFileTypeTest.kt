package com.github.yamanakahirofumi.siren

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.openapi.fileTypes.FileTypeManager

class MermaidFileTypeTest : BasePlatformTestCase() {
    fun testFileTypes() {
        val mermaidFile = myFixture.configureByText("test.mermaid", "graph TD")
        assertEquals(MermaidFileType.INSTANCE, mermaidFile.fileType)

        val mmdFile = myFixture.configureByText("test.mmd", "graph TD")
        assertEquals(MermaidFileType.INSTANCE, mmdFile.fileType)
    }

    fun testFileTypeRegistration() {
        val fileTypeManager = FileTypeManager.getInstance()
        assertEquals(MermaidFileType.INSTANCE, fileTypeManager.getFileTypeByExtension("mermaid"))
        assertEquals(MermaidFileType.INSTANCE, fileTypeManager.getFileTypeByExtension("mmd"))
    }
}
