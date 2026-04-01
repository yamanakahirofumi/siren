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

        injectStyles();
        renderAllMermaidBlocks();
        observeChanges();
    }

    function injectStyles() {
        if (document.getElementById('mermaid-zoom-styles')) return;
        const style = document.createElement('style');
        style.id = 'mermaid-zoom-styles';
        style.textContent = `
            .mermaid-wrapper {
                position: relative;
                margin-bottom: 20px;
            }
            .mermaid-zoom-controls {
                display: flex;
                position: absolute;
                top: 5px;
                left: 5px;
                z-index: 10;
                background: rgba(255, 255, 255, 0.7);
                border-radius: 4px;
                padding: 2px;
            }
            .theme-dark .mermaid-zoom-controls {
                background: rgba(0, 0, 0, 0.5);
            }
            .mermaid-zoom-controls button {
                padding: 2px 8px;
                cursor: pointer;
            }
            .mermaid-zoom-controls input {
                width: 45px;
                text-align: center;
                border: none;
                font-size: 12px;
            }
            .mermaid {
                overflow: auto;
                display: flex;
                justify-content: center;
                align-items: center;
            }
            .mermaid svg {
                width: auto;
                height: auto;
                max-width: none !important;
                max-height: none;
            }
        `;
        document.head.appendChild(style);
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

        const wrapper = document.createElement('div');
        wrapper.className = 'mermaid-wrapper';
        
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
            
            wrapper.appendChild(container);
            addZoomControls(wrapper, container, code, id);

            if (targetElement.parentNode) {
                targetElement.parentNode.replaceChild(wrapper, targetElement);
            }
            
            // Initial sizing
            sizeChange(container, 100);
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

    function addZoomControls(wrapper, container, code, id) {
        const controls = document.createElement('div');
        controls.className = 'mermaid-zoom-controls';
        
        const btnIn = document.createElement('button');
        btnIn.textContent = '+';
        
        const btnOut = document.createElement('button');
        btnOut.textContent = '-';
        
        const zoomInput = document.createElement('input');
        zoomInput.value = '100%';
        zoomInput.readOnly = true;
        
        let zoom = 100;
        const zoom_step = 20;
        
        btnIn.onclick = () => {
            zoom += zoom_step;
            updateZoom();
        };
        
        btnOut.onclick = () => {
            if (zoom <= zoom_step) return;
            zoom -= zoom_step;
            updateZoom();
        };
        
        async function updateZoom() {
            zoomInput.value = zoom + '%';
            sizeChange(container, zoom);
            
            // Re-render if necessary to apply size change properly
            // but for simple SVG scaling it might not be strictly needed if we just adjust container
            // preview.html calls mermaid_render() in size_change()
            const { svg } = await mermaid.render(id + '-zoom', code);
            container.innerHTML = svg;
            sizeChange(container, zoom);
        }
        
        controls.appendChild(btnIn);
        controls.appendChild(btnOut);
        controls.appendChild(zoomInput);
        wrapper.appendChild(controls);
    }

    function sizeChange(container, zoom) {
        const svg = container.querySelector('svg');
        if (!svg) return;
        const viewBox = svg.getAttribute('viewBox');
        if (!viewBox) return;
        
        const numbers = viewBox.split(' ').map(Number);
        container.style.width = (numbers[2] * zoom / 100) + "px";
        container.style.height = (numbers[3] * zoom / 100) + "px";
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
