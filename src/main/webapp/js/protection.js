// protection.js - Advanced Copy Prevention
(function() {
    'use strict';

    // ============================================================
    // 1. DISABLE RIGHT CLICK (Context Menu)
    // ============================================================
    document.addEventListener('contextmenu', function(e) {
        e.preventDefault();
        return false;
    });

    // Also prevent context menu on images and links
    document.addEventListener('mousedown', function(e) {
        if (e.button === 2) {
            e.preventDefault();
            return false;
        }
    });

    // ============================================================
    // 2. DISABLE TEXT SELECTION (CSS-based)
    // ============================================================
    var style = document.createElement('style');
    style.textContent = `
        body {
            -webkit-user-select: none !important;
            -moz-user-select: none !important;
            -ms-user-select: none !important;
            user-select: none !important;
        }
        
        /* Allow selection in input fields and textareas for usability */
        input, textarea, select, [contenteditable="true"] {
            -webkit-user-select: text !important;
            -moz-user-select: text !important;
            -ms-user-select: text !important;
            user-select: text !important;
        }
    `;
    document.head.appendChild(style);

    // ============================================================
    // 3. DISABLE COPY, CUT, PASTE EVENTS
    // ============================================================
    document.addEventListener('copy', function(e) {
        e.preventDefault();
        return false;
    });

    document.addEventListener('cut', function(e) {
        e.preventDefault();
        return false;
    });

    // Allow paste in input fields only
    document.addEventListener('paste', function(e) {
        var target = e.target;
        if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.tagName === 'SELECT') {
            return true;
        }
        e.preventDefault();
        return false;
    });

    // ============================================================
    // 4. DISABLE KEYBOARD SHORTCUTS
    // ============================================================
    document.addEventListener('keydown', function(e) {
        // Prevent Ctrl+C, Ctrl+X, Ctrl+V, Ctrl+U, Ctrl+S, Ctrl+P
        if (e.ctrlKey || e.metaKey) {
            var key = e.key.toLowerCase();
            if (key === 'c' || key === 'x' || key === 'v' ||
                key === 'u' || key === 's' || key === 'p' ||
                key === 'a' || key === 'f' || key === 'h') {
                // Allow Ctrl+A in input fields
                if (key === 'a' && (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA')) {
                    return true;
                }
                e.preventDefault();
                return false;
            }
        }

        // Prevent F12 (DevTools)
        if (e.key === 'F12') {
            e.preventDefault();
            return false;
        }

        // Prevent Ctrl+Shift+I (DevTools)
        if (e.ctrlKey && e.shiftKey && (e.key === 'I' || e.key === 'i')) {
            e.preventDefault();
            return false;
        }

        // Prevent Ctrl+Shift+J (DevTools Console)
        if (e.ctrlKey && e.shiftKey && (e.key === 'J' || e.key === 'j')) {
            e.preventDefault();
            return false;
        }

        // Prevent Ctrl+Shift+C (Inspect Element)
        if (e.ctrlKey && e.shiftKey && (e.key === 'C' || e.key === 'c')) {
            e.preventDefault();
            return false;
        }

        // Prevent Ctrl+U (View Source)
        if (e.ctrlKey && (e.key === 'U' || e.key === 'u')) {
            e.preventDefault();
            return false;
        }

        return true;
    });

    // ============================================================
    // 5. PREVENT DRAG AND DROP (copying content)
    // ============================================================
    document.addEventListener('dragstart', function(e) {
        e.preventDefault();
        return false;
    });

    document.addEventListener('drop', function(e) {
        e.preventDefault();
        return false;
    });

    // ============================================================
    // 6. DISABLE SELECTION BY MOUSE (additional layer)
    // ============================================================
    document.addEventListener('selectstart', function(e) {
        if (e.target.tagName !== 'INPUT' && e.target.tagName !== 'TEXTAREA' && e.target.tagName !== 'SELECT') {
            e.preventDefault();
            return false;
        }
    });

    // ============================================================
    // 7. BLOCK DEVTOOLS OPENING VIA KEYBOARD (Additional)
    // ============================================================
    var devtoolsOpen = false;
    var element = new Image();

    Object.defineProperty(element, 'id', {
        get: function() {
            devtoolsOpen = true;
            console.clear();
            console.log('%c🚫 Developer tools are disabled on this site.',
                'font-size: 18px; color: #f56565; font-weight: bold;');
            console.log('%cPlease close developer tools to continue.',
                'font-size: 14px; color: #7f8c8d;');
            return '';
        }
    });

    // Check periodically for DevTools
    setInterval(function() {
        devtoolsOpen = false;
        console.log('%c', element);
        if (devtoolsOpen) {
            // DevTools is open - redirect or show message
            document.body.innerHTML = `
                <div style="display: flex; justify-content: center; align-items: center; height: 100vh; 
                     font-family: 'Segoe UI', Arial, sans-serif; background: #f8f9fa; flex-direction: column; padding: 20px; text-align: center;">
                    <div style="background: white; padding: 40px; border-radius: 16px; max-width: 500px; 
                          box-shadow: 0 20px 60px rgba(0,0,0,0.1); border-top: 4px solid #f56565;">
                        <i class="fas fa-shield-alt" style="font-size: 48px; color: #f56565; margin-bottom: 20px; display: block;"></i>
                        <h1 style="color: #2c3e50; font-size: 24px; margin-bottom: 10px;">Developer Tools Detected</h1>
                        <p style="color: #7f8c8d; font-size: 16px; line-height: 1.6; margin-bottom: 20px;">
                            Please close developer tools (F12, Ctrl+Shift+I, or right-click Inspect) to continue using the application.
                        </p>
                        <button onclick="location.reload()" style="background: #3CA6A6; color: white; border: none; 
                              padding: 12px 30px; border-radius: 8px; font-size: 16px; cursor: pointer;">
                            <i class="fas fa-sync-alt"></i> Refresh Page
                        </button>
                    </div>
                </div>
            `;
        }
    }, 1000);

    // ============================================================
    // 8. PREVENT IMAGE DRAGGING
    // ============================================================
    document.querySelectorAll('img').forEach(function(img) {
        img.setAttribute('draggable', 'false');
        img.style.webkitUserDrag = 'none';
    });

    // ============================================================
    // 9. BLOCK CONTEXT MENU ON INPUT FIELDS (Prevent Right-click Copy)
    // ============================================================
    document.querySelectorAll('input, textarea, select').forEach(function(el) {
        el.addEventListener('contextmenu', function(e) {
            e.preventDefault();
            return false;
        });
    });

    // ============================================================
    // 10. PRINT PREVENTION (Optional)
    // ============================================================
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && (e.key === 'p' || e.key === 'P')) {
            e.preventDefault();
            showPrintWarning();
            return false;
        }
    });

    function showPrintWarning() {
        // Check if toast already exists
        var existing = document.querySelector('.print-warning-toast');
        if (existing) {
            existing.remove();
        }

        var toast = document.createElement('div');
        toast.className = 'print-warning-toast';
        toast.style.cssText = `
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 30px 40px;
            border-radius: 16px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            z-index: 9999999;
            max-width: 400px;
            text-align: center;
            font-family: 'Segoe UI', Arial, sans-serif;
            border-top: 4px solid #ecc94b;
            animation: printFadeIn 0.3s ease;
        `;

        toast.innerHTML = `
            <i class="fas fa-print" style="font-size: 40px; color: #ecc94b; margin-bottom: 15px; display: block;"></i>
            <h3 style="color: #2c3e50; margin-bottom: 10px; font-size: 20px;">Printing Disabled</h3>
            <p style="color: #7f8c8d; font-size: 14px; line-height: 1.6; margin-bottom: 20px;">
                Printing is currently disabled for this page. Please use the application's print functionality if available.
            </p>
            <button onclick="this.closest('.print-warning-toast').remove()" style="background: #3CA6A6; color: white; border: none; 
                  padding: 10px 25px; border-radius: 8px; font-size: 14px; cursor: pointer;">
                <i class="fas fa-times"></i> Close
            </button>
        `;

        document.body.appendChild(toast);

        // Inject animation styles
        if (!document.getElementById('printAnimationStyles')) {
            var style = document.createElement('style');
            style.id = 'printAnimationStyles';
            style.textContent = `
                @keyframes printFadeIn {
                    from { opacity: 0; transform: translate(-50%, -50%) scale(0.9); }
                    to { opacity: 1; transform: translate(-50%, -50%) scale(1); }
                }
            `;
            document.head.appendChild(style);
        }

        // Auto-remove after 4 seconds
        setTimeout(function() {
            var el = document.querySelector('.print-warning-toast');
            if (el) el.remove();
        }, 4000);
    }

    // ============================================================
    // 11. PREVENT MIDDLE-CLICK PASTE (On some browsers)
    // ============================================================
    document.addEventListener('auxclick', function(e) {
        if (e.button === 1) {
            e.preventDefault();
            return false;
        }
    });

    // ============================================================
    // 12. OVERRIDE console.log (Prevent debugging via console)
    // ============================================================
    if (typeof console !== 'undefined') {
        var originalConsole = console.log;
        console.log = function() {
            // Allow logging but don't show sensitive info
            // You can comment this out for debugging
            // originalConsole.apply(console, arguments);
        };
    }

    console.log('%c🔒 Content Protection Active', 'font-size: 16px; color: #3CA6A6; font-weight: bold;');
    console.log('%cCopying, printing, and developer tools are disabled.', 'font-size: 13px; color: #7f8c8d;');

})();