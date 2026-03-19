(function() {
    function initMermaid() {
        if (typeof mermaid === 'undefined') {
            setTimeout(initMermaid, 100);
            return;
        }

        const isDarkMode = document.body.classList.contains('theme-dark') ||
                           window.matchMedia('(prefers-color-scheme: dark)').matches;

        mermaid.initialize({
            startOnLoad: false,
            theme: isDarkMode ? 'dark' : 'default',
            securityLevel: 'loose',
        });

        renderAllMermaidBlocks();
        observeChanges();
    }

    async function renderAllMermaidBlocks() {
        const blocks = document.querySelectorAll('code.language-mermaid');
        for (let i = 0; i < blocks.length; i++) {
            await renderBlock(blocks[i], i);
        }
    }

    async function renderBlock(block, index) {
        if (block.getAttribute('data-mermaid-rendered')) return;
        block.setAttribute('data-mermaid-rendered', 'true');

        const container = document.createElement('div');
        container.className = 'mermaid';
        container.setAttribute('data-mermaid-rendered', 'true');
        const id = 'mermaid-diagram-' + Date.now() + '-' + index;

        const parentPre = block.closest('pre');
        const targetElement = (parentPre && parentPre !== block) ? parentPre : block;

        try {
            let code = block.innerText || block.textContent;
            code = code.trim();

            const { svg } = await mermaid.render(id, code);
            container.innerHTML = svg;
            if (targetElement.parentNode) {
                targetElement.parentNode.replaceChild(container, targetElement);
            }
        } catch (error) {
            const errorElement = document.createElement('pre');
            errorElement.style.color = 'red';
            errorElement.textContent = 'Mermaid Error: ' + error.message;
            errorElement.setAttribute('data-mermaid-rendered', 'true');
            if (targetElement.parentNode) {
                targetElement.parentNode.replaceChild(errorElement, targetElement);
            }
        }
    }

    function observeChanges() {
        let timer = null;
        const observer = new MutationObserver((mutations) => {
            let shouldRender = false;
            mutations.forEach((mutation) => {
                mutation.addedNodes.forEach((node) => {
                    if (node.nodeType === Node.ELEMENT_NODE) {
                        const selector = 'code.language-mermaid, div.mermaid, pre.mermaid';
                        if ((node.matches(selector) || node.querySelector(selector)) && !node.closest('[data-mermaid-rendered]')) {
                            shouldRender = true;
                        }
                    }
                });
                if (mutation.type === 'characterData' || (mutation.type === 'childList' && mutation.target.nodeType === Node.ELEMENT_NODE)) {
                    const block = mutation.target.closest && mutation.target.closest('code.language-mermaid, div.mermaid, pre.mermaid');
                    if (block && block.getAttribute('data-mermaid-rendered')) {
                        block.removeAttribute('data-mermaid-rendered');
                        shouldRender = true;
                    }
                }
            });
            if (shouldRender) {
                if (timer) clearTimeout(timer);
                timer = setTimeout(renderAllMermaidBlocks, 100);
            }
        });

        observer.observe(document.body, {
            childList: true,
            subtree: true,
            characterData: true
        });

        // Observe theme changes
        const themeObserver = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
                if (mutation.attributeName === 'class') {
                    const isDarkMode = document.body.classList.contains('theme-dark');
                    mermaid.initialize({
                        theme: isDarkMode ? 'dark' : 'default'
                    });
                }
            });
        });
        themeObserver.observe(document.body, { attributes: true });
    }

    if (document.readyState === 'complete' || document.readyState === 'interactive') {
        setTimeout(initMermaid, 500); // Give it a bit more time for other scripts
    } else {
        document.addEventListener('DOMContentLoaded', () => setTimeout(initMermaid, 500));
    }
})();
