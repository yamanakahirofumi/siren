package com.github.yamanakahirofumi.siren

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class MermaidMarkdownExtensionTest : BasePlatformTestCase() {

    fun testExtensionScriptContent() {
        val extension = com.github.yamanakahirofumi.siren.markdown.MermaidBrowserPreviewExtension()
        val scripts = extension.scripts

        assertTrue("Extension should provide mermaid.min.js and render-mermaid.js", scripts.size >= 2)
        assertTrue("One script should be mermaid.min.js", scripts.any { it.endsWith("mermaid.min.js") })
        assertTrue("One script should be render-mermaid.js", scripts.any { it.endsWith("render-mermaid.js") })

        val resource = extension.loadResource("mermaid-siren/render-mermaid.js")
        assertNotNull("Should be able to load render-mermaid.js", resource)
        val content = String(resource!!.content)
        assertTrue("Script should contain Siren identification", content.contains("Siren Mermaid extension loading"))
        assertTrue("Script should contain mermaid.render", content.contains("mermaid.render"))
        assertTrue("Script should handle multiple selectors", content.contains("selectors"))
    }
}
