package com.github.yamanakahirofumi.siren.server

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
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

        registerHandlers()

        server.start()
        logger.info("Mermaid preview server started on port $port")

        Disposer.register(parentDisposable, this)
    }

    private fun registerHandlers() {
        server.createContext("/", RootHandler())
        server.createContext("/preview", PreviewHandler())
        server.createContext("/mermaid.min.js", ResourceHandler("/mermaid.min.js", "application/javascript"))
    }

    fun updateDiagram(id: String, content: String) {
        diagramContents[id] = content
    }

    fun getPreviewUrl(id: String): String = "http://localhost:$port/preview?id=$id&_t=${System.currentTimeMillis()}"

    private fun findAvailablePort(): Int = ServerSocket(0).use { it.localPort }

    private inner class RootHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            sendResponse(exchange, "Mermaid Preview Server")
        }
    }

    private inner class PreviewHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val query = exchange.requestURI.query
            val id = query?.split("&")?.find { it.startsWith("id=") }?.split("=")?.getOrNull(1)

            if (id == null || !diagramContents.containsKey(id)) {
                exchange.sendResponseHeaders(404, 0)
                exchange.responseBody.close()
                return
            }

            val diagramContent = diagramContents[id] ?: ""
            val templateHtml = this.javaClass.getResourceAsStream("/preview.html")
                ?.readBytes()?.toString(StandardCharsets.UTF_8) ?: ""

            val html = templateHtml.replace("%diagram%", diagramContent)
            exchange.responseHeaders.set("Content-Type", "text/html")
            sendResponse(exchange, html)
        }
    }

    private inner class ResourceHandler(private val resourcePath: String, private val contentType: String) : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            this.javaClass.getResourceAsStream(resourcePath).use { stream ->
                if (stream == null) {
                    exchange.sendResponseHeaders(404, 0)
                    exchange.responseBody.close()
                    return
                }

                val resourceBytes = stream.readBytes()
                exchange.responseHeaders.set("Content-Type", contentType)
                exchange.sendResponseHeaders(200, resourceBytes.size.toLong())
                exchange.responseBody.use { it.write(resourceBytes) }
            }
        }
    }

    private fun sendResponse(exchange: HttpExchange, response: String) {
        val bytes = response.toByteArray(StandardCharsets.UTF_8)
        exchange.sendResponseHeaders(200, bytes.size.toLong())
        exchange.responseBody.use { it.write(bytes) }
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
