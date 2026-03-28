package com.github.yamanakahirofumi.siren.editor

import com.github.yamanakahirofumi.siren.server.MermaidPreviewServer
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.vfs.VirtualFile
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.contains
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.startsWith
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.beans.PropertyChangeListener
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

    fun testEditorWithFileOnly() {
        val file = mock(VirtualFile::class.java)
        whenever(file.contentsToByteArray()).thenReturn("graph TD".toByteArray(Charsets.UTF_8))

        val mockBrowser = mock(JBCefBrowser::class.java)
        val mockServer = mock(MermaidPreviewServer::class.java)

        val editor = MermaidPreviewEditor(project, file, mockBrowser, mockServer)
        try {
            assertNotNull(editor)
            // Initial update uses the text from the file
            verify(mockServer).updateDiagram(anyString(), startsWith("graph TD"))
        } finally {
            Disposer.dispose(editor)
        }
    }

    fun testErrorHtmlWithNullMessage() {
        val file = myFixture.configureByText("test_error.mermaid", "graph TD").virtualFile
        val mockBrowser = mock(JBCefBrowser::class.java)
        val mockServer = mock(MermaidPreviewServer::class.java)

        // Force an exception with null message
        whenever(mockServer.updateDiagram(anyString(), anyString())).thenThrow(RuntimeException(null as String?))

        val editor = MermaidPreviewEditor(project, file, mockBrowser, mockServer)
        try {
            verify(mockBrowser).loadHTML(contains("Unknown error"))
        } finally {
            Disposer.dispose(editor)
        }
    }

    fun testFileEditorInterfaceMethods() {
        val file = myFixture.configureByText("test_interface.mermaid", "graph TD").virtualFile
        val mockBrowser = mock(JBCefBrowser::class.java)
        val mockServer = mock(MermaidPreviewServer::class.java)
        val editor = MermaidPreviewEditor(project, file, mockBrowser, mockServer)

        try {
            // These should not crash
            editor.setState(mock(FileEditorState::class.java))
            assertFalse(editor.isModified)
            assertTrue(editor.isValid)
            assertNull(editor.currentLocation)

            val listener = mock(PropertyChangeListener::class.java)
            editor.addPropertyChangeListener(listener)
            editor.removePropertyChangeListener(listener)
        } finally {
            Disposer.dispose(editor)
        }
    }

    fun testDisposeWithNullDocument() {
        val file = mock(VirtualFile::class.java)
        whenever(file.contentsToByteArray()).thenReturn("graph TD".toByteArray(Charsets.UTF_8))

        val mockBrowser = mock(JBCefBrowser::class.java)
        val mockServer = mock(MermaidPreviewServer::class.java)

        val editor = MermaidPreviewEditor(project, file, mockBrowser, mockServer)
        // boundDocument will be null because it's a mock file not in FileDocumentManager

        Disposer.dispose(editor)

        verify(mockBrowser).dispose()
        verify(mockServer).dispose()
    }
}
