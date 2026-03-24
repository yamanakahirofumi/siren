package com.github.yamanakahirofumi.siren.editor

import com.github.yamanakahirofumi.siren.server.MermaidPreviewServer
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.swing.JPanel
import org.mockito.Mockito.`when` as whenever

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

    fun testEditorWithMocks() {
        val file = myFixture.configureByText("test.mermaid", "graph TD").virtualFile
        val mockBrowser = mock(JBCefBrowser::class.java)
        val mockServer = mock(MermaidPreviewServer::class.java)
        val mockComponent = JPanel()
        whenever(mockBrowser.component).thenReturn(mockComponent)
        whenever(mockServer.getPreviewUrl(anyString())).thenReturn("http://localhost/preview")

        val editor = MermaidPreviewEditor(project, file, mockBrowser, mockServer)
        try {
            assertEquals("Mermaid Preview", editor.name)
            assertEquals(file, editor.file)
            assertTrue(editor.isValid)
            assertFalse(editor.isModified)
            assertNull(editor.currentLocation)
            assertEquals(mockComponent, editor.component)
            assertEquals(mockComponent, editor.preferredFocusedComponent)

            // Verify initial update
            verify(mockServer).updateDiagram(anyString(), anyString())
            verify(mockBrowser).loadURL(anyString())

            // Test document change
            val document = FileDocumentManager.getInstance().getDocument(file)!!
            WriteCommandAction.runWriteCommandAction(project) {
                document.setText("graph LR\nA-->B")
            }
            verify(mockServer, atLeastOnce()).updateDiagram(anyString(), anyString())

            // Test error handling
            whenever(mockServer.updateDiagram(anyString(), anyString())).thenThrow(RuntimeException("Test Error"))
            WriteCommandAction.runWriteCommandAction(project) {
                document.setText("graph LR\nA-->C")
            }
            verify(mockBrowser).loadHTML(anyString())

            // Test property change listeners (no-op but for coverage)
            val listener = java.beans.PropertyChangeListener {}
            editor.addPropertyChangeListener(listener)
            editor.removePropertyChangeListener(listener)

            // Test setState
            editor.setState(mock(com.intellij.openapi.fileEditor.FileEditorState::class.java))

        } finally {
            Disposer.dispose(editor)
        }

        // Verify disposal
        verify(mockBrowser).dispose()
        verify(mockServer).dispose()
    }

    fun testEditorWithoutDocument() {
        // Create a file without a document in FileDocumentManager (or at least simulate that branch)
        val file = myFixture.addFileToProject("test2.mermaid", "graph TD").virtualFile
        val mockBrowser = mock(JBCefBrowser::class.java)
        val mockServer = mock(MermaidPreviewServer::class.java)

        // We want boundDocument to be null.
        // FileDocumentManager.getInstance().getDocument(file) usually returns a document for files in the project.
        // But if we use a mock file or a file outside the project it might be null.

        val editor = MermaidPreviewEditor(project, file, mockBrowser, mockServer)
        try {
            assertNotNull(editor)
            verify(mockServer).updateDiagram(anyString(), anyString())
        } finally {
            Disposer.dispose(editor)
        }
    }
}
