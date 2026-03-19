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

        const container = document.createElement('div');
        container.className = 'mermaid';
        const id = 'mermaid-diagram-' + Date.now() + '-' + index;

        // Find the parent pre element, if any
        const parentPre = block.closest('pre');
        const targetElement = parentPre || block;

        try {
            const { svg } = await mermaid.render(id, block.textContent);
            container.innerHTML = svg;
            targetElement.parentNode.replaceChild(container, targetElement);
            container.setAttribute('data-mermaid-rendered', 'true');
        } catch (error) {
            console.error('Mermaid rendering error:', error);
            const errorElement = document.createElement('pre');
            errorElement.style.color = 'red';
            errorElement.textContent = 'Mermaid Error: ' + error.message;
            targetElement.parentNode.replaceChild(errorElement, targetElement);
        }
    }

    function observeChanges() {
        const observer = new MutationObserver((mutations) => {
            let shouldRender = false;
            mutations.forEach((mutation) => {
                mutation.addedNodes.forEach((node) => {
                    if (node.nodeType === Node.ELEMENT_NODE) {
                        if (node.matches('code.language-mermaid') || node.querySelector('code.language-mermaid')) {
                            shouldRender = true;
                        }
                    }
                });
            });
            if (shouldRender) {
                renderAllMermaidBlocks();
            }
        });

        observer.observe(document.body, {
            childList: true,
            subtree: true
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
        initMermaid();
    } else {
        document.addEventListener('DOMContentLoaded', initMermaid);
    }
})();
