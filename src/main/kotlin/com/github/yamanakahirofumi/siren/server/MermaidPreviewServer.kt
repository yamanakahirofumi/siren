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
        // Find an available port
        port = findAvailablePort()

        // Create and start the server
        server = HttpServer.create(InetSocketAddress(port), 0)
        server.executor = executor

        // Register handlers
        server.createContext("/", RootHandler())
        server.createContext("/preview", PreviewHandler())
        server.createContext("/mermaid.min.js", ResourceHandler("/mermaid.min.js", "application/javascript"))

        server.start()
        logger.info("Mermaid preview server started on port $port")

        Disposer.register(parentDisposable, this)
    }

    /**
     * Returns the base URL for this server.
     */
    fun getBaseUrl(): String = "http://localhost:$port"

    /**
     * Updates the diagram content for the given ID.
     */
    fun updateDiagram(id: String, content: String) {
        diagramContents[id] = content
    }

    /**
     * Gets the preview URL for the given diagram ID.
     */
    fun getPreviewUrl(id: String): String = "${"http://localhost:$port"}/preview?id=$id"

    /**
     * Finds an available port to use.
     */
    private fun findAvailablePort(): Int {
        ServerSocket(0).use { socket ->
            return socket.localPort
        }
    }

    /**
     * Handler for the root path.
     */
    private inner class RootHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val response = "Mermaid Preview Server"
            exchange.sendResponseHeaders(200, response.length.toLong())
            val os = exchange.responseBody
            os.write(response.toByteArray())
            os.close()
        }
    }

    /**
     * Handler for the preview path.
     */
    private inner class PreviewHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val query = exchange.requestURI.query
            val id = query?.split("=")?.getOrNull(1)

            if (id == null || !diagramContents.containsKey(id)) {
                exchange.sendResponseHeaders(404, 0)
                exchange.responseBody.close()
                return
            }

            val diagramContent = diagramContents[id] ?: ""

            // Load the template HTML
            val templateHtml = this.javaClass.getResourceAsStream("/preview.html")?.readBytes()
                ?.toString(StandardCharsets.UTF_8) ?: ""

            // Replace the placeholder with the actual diagram content
            val html = templateHtml.replace("%diagram%", diagramContent)

            // Send the response
            exchange.responseHeaders.set("Content-Type", "text/html")
            sendResponse(exchange, html)
        }
    }

    /**
     * Handler for serving static resources.
     */
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

    /**
     * Helper method to send a response.
     */
    private fun sendResponse(exchange: HttpExchange, response: String) {
        exchange.sendResponseHeaders(200, response.length.toLong())
        val os: OutputStream = exchange.responseBody
        os.write(response.toByteArray())
        os.close()
    }

    /**
     * Stops the server when disposed.
     */
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