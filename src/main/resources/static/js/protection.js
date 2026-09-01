// protection.js - Copy prevention
(function() {
    'use strict';

    // Disable right click
    document.addEventListener('contextmenu', function(e) {
        e.preventDefault();
        return false;
    });

    // Disable copy
    document.addEventListener('copy', function(e) {
        e.preventDefault();
        return false;
    });

    // Disable keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey || e.metaKey) {
            if (e.key === 'c' || e.key === 'C' ||
                e.key === 'x' || e.key === 'X' ||
                e.key === 'v' || e.key === 'V') {
                e.preventDefault();
                return false;
            }
        }
        if (e.key === 'F12' || (e.ctrlKey && e.shiftKey && e.key === 'I')) {
            e.preventDefault();
            return false;
        }
    });
})();