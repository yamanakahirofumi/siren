package com.github.yamanakahirofumi.siren.editor

import com.github.yamanakahirofumi.siren.server.MermaidPreviewServer
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefBrowser
import java.beans.PropertyChangeListener
import java.util.UUID
import javax.swing.JComponent

class MermaidPreviewEditor @JvmOverloads constructor(
    private val project: Project,
    private val file: VirtualFile,
    private val browser: JBCefBrowser = JBCefBrowser(),
    server: MermaidPreviewServer? = null
) : UserDataHolderBase(), FileEditor {

    private val myServer: MermaidPreviewServer = server ?: MermaidPreviewServer(this)
    private val diagramId: String = UUID.randomUUID().toString()
    private var boundDocumentListener: DocumentListener? = null
    private var boundDocument = FileDocumentManager.getInstance().getDocument(file)

    init {
        val initialText = setupDocumentListener()
        updatePreview(initialText)
    }

    private fun setupDocumentListener(): String {
        val documentListener = object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                if (event.document == boundDocument) {
                    updatePreview(event.document.text)
                }
            }
        }
        boundDocumentListener = documentListener

        val doc = boundDocument
        return if (doc != null) {
            doc.addDocumentListener(documentListener)
            doc.text
        } else {
            file.contentsToByteArray().toString(Charsets.UTF_8)
        }
    }

    private fun updatePreview(text: String) {
        try {
            myServer.updateDiagram(diagramId, text)
            browser.loadURL(myServer.getPreviewUrl(diagramId))
        } catch (e: Exception) {
            browser.loadHTML(getErrorHtml(e.message))
        }
    }

    private fun getErrorHtml(message: String?): String {
        val escapedMessage = message?.replace("<", "&lt;")?.replace(">", "&gt;") ?: "Unknown error"
        return """
            <!DOCTYPE html>
            <html>
            <body>
                <div style="color: red; padding: 20px;">
                    Error rendering diagram: $escapedMessage
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    override fun getComponent(): JComponent = browser.component

    override fun getPreferredFocusedComponent(): JComponent = browser.component

    override fun getName(): String = "Mermaid Preview"

    override fun getFile(): VirtualFile = file

    override fun setState(state: FileEditorState) {}

    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun dispose() {
        boundDocumentListener?.let { listener ->
            boundDocument?.removeDocumentListener(listener)
        }

        Disposer.dispose(browser)
        Disposer.dispose(myServer)
    }
}
