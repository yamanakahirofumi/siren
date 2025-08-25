package com.github.yamanakahirofumi.siren.editor

import com.github.yamanakahirofumi.siren.server.MermaidPreviewServer
import com.intellij.openapi.editor.EditorFactory
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
import javax.swing.JComponent
import java.util.UUID

class MermaidPreviewEditor(private val project: Project, private val file: VirtualFile) :
    UserDataHolderBase(), FileEditor {

    private val browser: JBCefBrowser = JBCefBrowser()
    private val server: MermaidPreviewServer = MermaidPreviewServer(this)
    private val diagramId: String = UUID.randomUUID().toString()
    private var boundDocumentListener: DocumentListener? = null
    private var boundDocument = FileDocumentManager.getInstance().getDocument(file)

    init {
        val documentListener = object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                // 編集中のドキュメントに限定
                if (event.document == boundDocument) {
                    updatePreview(event.document.text)
                }
            }
        }
        boundDocumentListener = documentListener

        // 実ファイルに紐づく Document が取得できた場合はそれを監視する
        val initialText: String = if (boundDocument != null) {
            boundDocument!!.addDocumentListener(documentListener)
            boundDocument!!.text
        } else {
            // 取得できないケース（バイナリ/未ロードなど）のフォールバック
            file.contentsToByteArray().toString(Charsets.UTF_8)
        }

        updatePreview(initialText)
    }

    private fun updatePreview(text: String) {
        try {
            // Update the diagram content on the server
            server.updateDiagram(diagramId, text)

            // Load the preview URL in the browser
            browser.loadURL(server.getPreviewUrl(diagramId))
        } catch (e: Exception) {
            // エラーハンドリング - エラーメッセージを表示
            val errorHtml = """
                <!DOCTYPE html>
                <html>
                <body>
                    <div style="color: red; padding: 20px;">
                        Error rendering diagram: ${e.message?.replace("<", "&lt;")?.replace(">", "&gt;") ?: "Unknown error"}
                    </div>
                </body>
                </html>
            """.trimIndent()
            browser.loadHTML(errorHtml)
        }
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
        // DocumentListener を確実に解除
        val listener = boundDocumentListener
        val doc = boundDocument
        if (listener != null && doc != null) {
            doc.removeDocumentListener(listener)
        }

        Disposer.dispose(browser)
        Disposer.dispose(server)
    }
}
