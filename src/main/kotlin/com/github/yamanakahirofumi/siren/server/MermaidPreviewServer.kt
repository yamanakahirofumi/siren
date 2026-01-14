package com.github.yamanakahirofumi.siren.server

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * A simple HTTP server for serving Mermaid preview content.
 */
class MermaidPreviewServer(parentDisposable: Disposable) : Disposable {
    private val logger = Logger.getInstance(MermaidPreviewServer::class.java)
    private val server: HttpServer
    private val port: Int
    private val diagramContents = ConcurrentHashMap<String, String>()
    private val executor = Executors.newSingleThreadExecutor()

    init {
        port = findAvailablePort()

        server = HttpServer.create(InetSocketAddress(port), 0)
        server.executor = executor

        // Register handlers
        server.createContext("/", RootHandler())
        server.createContext("/preview", ResourceHandler("/preview.html", "text/html"))
        server.createContext("/diagram", DiagramsHandler())
        server.createContext("/mermaid.min.js", ResourceHandler("/mermaid.min.js", "text/javascript; charset=utf-8"))

        server.start()
        logger.info("Mermaid preview server started on port $port")

        Disposer.register(parentDisposable, this)
    }

    fun getBaseUrl(): String = "http://localhost:$port"

    fun updateDiagram(id: String, content: String) {
        diagramContents[id] = content
    }

    fun getPreviewUrl(id: String): String = "${"http://localhost:$port"}/preview?id=$id"

    private fun findAvailablePort(): Int {
        ServerSocket(0).use { socket ->
            return socket.localPort
        }
    }

    private inner class RootHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val response = "Mermaid Preview Server"
            exchange.sendResponseHeaders(200, response.length.toLong())
            val os = exchange.responseBody
            os.write(response.toByteArray())
            os.close()
        }
    }

    private inner class DiagramsHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val query = exchange.requestURI.query
            val id = query?.split("=")?.getOrNull(1)
            if (id == null || !diagramContents.containsKey(id)) {
                exchange.sendResponseHeaders(404, 0)
                exchange.responseBody.close()
                return
            }
            exchange.responseHeaders.set("Content-Type", "application/json; charset=utf-8")
            val diagramContent = diagramContents[id] ?: ""
            val response = """{"content": "${escapeJson(diagramContent)}"}"""
            sendResponse(exchange, response)
        }

        private fun escapeJson(text: String): String {
            return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
        }
    }

    private inner class ResourceHandler(private val resourcePath: String, private val contentType: String) :
        HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val resourceStream = this.javaClass.getResourceAsStream(resourcePath)

            if (resourceStream == null) {
                exchange.sendResponseHeaders(404, 0)
                exchange.responseBody.close()
                return
            }

            val resourceBytes = resourceStream.readBytes()
            resourceStream.close()

            exchange.responseHeaders.set("Content-Type", contentType)
            exchange.sendResponseHeaders(200, resourceBytes.size.toLong())
            val os = exchange.responseBody
            os.write(resourceBytes)
            os.close()
        }
    }

    private fun sendResponse(exchange: HttpExchange, response: String) {
        exchange.sendResponseHeaders(200, response.length.toLong())
        val os: OutputStream = exchange.responseBody
        os.write(response.toByteArray())
        os.close()
    }

    override fun dispose() {
        try {
            server.stop(0)
            executor.shutdown()
            logger.info("Mermaid preview server stopped")
        } catch (e: Exception) {
            logger.error("Error stopping Mermaid preview server", e)
        }
    }
}