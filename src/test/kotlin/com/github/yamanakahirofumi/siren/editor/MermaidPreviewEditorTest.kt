package com.github.yamanakahirofumi.siren.editor

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.ui.jcef.JBCefApp

class MermaidPreviewEditorTest : BasePlatformTestCase() {

    private var jcefSupported = false

    override fun setUp() {
        super.setUp()
        jcefSupported = JBCefApp.isSupported()
    }

    fun testEditorProperties() {
        if (!jcefSupported) {
            println("JBCef is not supported, skipping testEditorProperties")
            return
        }
        val file = myFixture.configureByText("test.mermaid", "graph TD").virtualFile
        val editor = MermaidPreviewEditor(project, file)
        try {
            assertEquals("Mermaid Preview", editor.name)
            assertEquals(file, editor.file)
            assertTrue(editor.isValid)
            assertFalse(editor.isModified)
            assertNull(editor.currentLocation)
        } finally {
            Disposer.dispose(editor)
        }
    }

    fun testEditorComponent() {
        if (!jcefSupported) {
            println("JBCef is not supported, skipping testEditorComponent")
            return
        }
        val file = myFixture.configureByText("test.mermaid", "graph TD").virtualFile
        val editor = MermaidPreviewEditor(project, file)
        try {
            assertNotNull(editor.component)
            assertNotNull(editor.preferredFocusedComponent)
        } finally {
            Disposer.dispose(editor)
        }
    }

    fun testDocumentChangeUpdatesPreview() {
        if (!jcefSupported) {
            println("JBCef is not supported, skipping testDocumentChangeUpdatesPreview")
            return
        }
        val file = myFixture.configureByText("test.mermaid", "graph TD").virtualFile
        val editor = MermaidPreviewEditor(project, file)
        val document = FileDocumentManager.getInstance().getDocument(file)!!

        try {
            WriteCommandAction.runWriteCommandAction(project) {
                document.setText("graph LR\nA-->B")
            }
            // Logic-wise, this triggers updatePreview.
            // We can't easily verify the internal server state without making it accessible,
            // but we can ensure no crashes occur.
            assertNotNull(editor.component)
        } finally {
            Disposer.dispose(editor)
        }
    }

    fun testDispose() {
        if (!jcefSupported) {
            println("JBCef is not supported, skipping testDispose")
            return
        }
        val file = myFixture.configureByText("test.mermaid", "graph TD").virtualFile
        val editor = MermaidPreviewEditor(project, file)

        // Ensure no exception on double dispose or normal dispose
        Disposer.dispose(editor)
    }
}
