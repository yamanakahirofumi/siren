package com.github.yamanakahirofumi.siren.markdown

import org.intellij.plugins.markdown.extensions.MarkdownBrowserPreviewExtension
import com.intellij.util.ResourceUtil

class MermaidBrowserPreviewExtension : MarkdownBrowserPreviewExtension {
    override fun dispose() {}

    override val scripts: List<String>
        get() {
            val mermaidJs = ResourceUtil.loadText(ResourceUtil.getResource(javaClass.classLoader, "", "mermaid.min.js"))
            val renderScript = """
                (function() {
                    function renderMermaid() {
                        if (typeof mermaid === 'undefined') {
                            return;
                        }
                        mermaid.initialize({ startOnLoad: false, theme: 'default' });
                        const blocks = document.querySelectorAll('pre > code.language-mermaid');
                        blocks.forEach((block, index) => {
                            const pre = block.parentElement;
                            if (pre.style.display === 'none') return;

                            const container = document.createElement('div');
                            container.className = 'mermaid';
                            container.style.display = 'flex';
                            container.style.justifyContent = 'center';
                            container.style.background = 'white';
                            container.id = 'mermaid-' + index;
                            const content = block.textContent;

                            pre.style.display = 'none';
                            pre.parentNode.insertBefore(container, pre);

                            mermaid.render('mermaid-svg-' + index, content).then(({svg}) => {
                                container.innerHTML = svg;
                            }).catch(err => {
                                console.error('Mermaid render error:', err);
                                container.innerText = 'Error rendering Mermaid diagram';
                                pre.style.display = 'block';
                            });
                        });
                    }

                    if (document.readyState === 'complete') {
                        renderMermaid();
                    } else {
                        window.addEventListener('load', renderMermaid);
                    }

                    // MutationObserver to handle dynamic updates in preview
                    let timeout;
                    const observer = new MutationObserver((mutations) => {
                        clearTimeout(timeout);
                        timeout = setTimeout(renderMermaid, 200);
                    });
                    observer.observe(document.body, { childList: true, subtree: true });
                })();
            """.trimIndent()
            return listOf(mermaidJs, renderScript)
        }

    override val styles: List<String>
        get() = emptyList()
}
