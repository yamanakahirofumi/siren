(function() {
    console.log('Siren Mermaid extension loading...');

    let isInitialized = false;

    function initializeMermaid() {
        if (typeof mermaid === 'undefined') return false;

        const isDarkMode = document.body.classList.contains('theme-dark') ||
                         window.matchMedia('(prefers-color-scheme: dark)').matches;

        console.log('Initializing Mermaid (dark mode: ' + isDarkMode + ')...');
        mermaid.initialize({
            startOnLoad: false,
            theme: isDarkMode ? 'dark' : 'default',
            securityLevel: 'loose'
        });
        isInitialized = true;
        return true;
    }

    function renderMermaid() {
        if (!isInitialized && !initializeMermaid()) {
            console.log('Mermaid not yet defined, retrying...');
            setTimeout(renderMermaid, 200);
            return;
        }

        // Support multiple common selectors for mermaid blocks
        const selectors = [
            'pre > code.language-mermaid',
            'pre.mermaid',
            'div.mermaid'
        ];

        selectors.forEach(selector => {
            const blocks = document.querySelectorAll(selector);
            if (blocks.length > 0) {
                console.log('Found ' + blocks.length + ' mermaid blocks with selector: ' + selector);
            }

            blocks.forEach((block, index) => {
                // Skip if already processed or inside a processed container
                if (block.getAttribute('data-mermaid-processed') ||
                    block.closest('[data-mermaid-processed]')) return;

                const container = document.createElement('div');
                container.className = 'mermaid-siren-container';
                container.style.display = 'flex';
                container.style.justifyContent = 'center';
                container.style.padding = '10px';
                container.setAttribute('data-mermaid-processed', 'true');

                const id = 'mermaid-siren-' + Math.random().toString(36).substr(2, 9);
                container.id = id;

                const content = block.textContent;

                // For pre > code, we want to hide the pre
                const elementToHide = block.tagName === 'CODE' ? block.parentElement : block;
                elementToHide.style.display = 'none';
                elementToHide.setAttribute('data-mermaid-processed', 'true');

                elementToHide.parentNode.insertBefore(container, elementToHide);

                console.log('Rendering block ' + id);
                mermaid.render(id + '-svg', content).then(({svg}) => {
                    container.innerHTML = svg;
                }).catch(err => {
                    console.error('Mermaid render error:', err);
                    container.innerText = 'Error rendering Mermaid diagram: ' + err.message;
                    elementToHide.style.display = 'block';
                });
            });
        });
    }

    if (document.readyState === 'complete' || document.readyState === 'interactive') {
        renderMermaid();
    } else {
        window.addEventListener('load', renderMermaid);
    }

    let timeout;
    const observer = new MutationObserver((mutations) => {
        clearTimeout(timeout);
        timeout = setTimeout(renderMermaid, 300);
    });
    observer.observe(document.body, { childList: true, subtree: true });
})();
