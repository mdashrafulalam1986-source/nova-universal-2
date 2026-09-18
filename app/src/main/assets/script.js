document.addEventListener('DOMContentLoaded', () => {
    const statusText = document.getElementById('statusText');
    const brandSelect = document.getElementById('brandSelect');

    // IR Signal Emitter Function (Triggers Android Native IR)
    function sendIRCommand(commandName) {
        const selectedBrand = brandSelect ? brandSelect.value : 'Universal';
        
        // Show user feedback on screen
        if (statusText) {
            statusText.innerText = `Sending: ${commandName} (${selectedBrand})`;
            statusText.style.color = '#38bdf8';
            
            setTimeout(() => {
                statusText.innerText = 'Ready';
                statusText.style.color = '#94a3b8';
            }, 1500);
        }

        // Bridge to Android Native IR Emitter if available
        if (window.AndroidIR && typeof window.AndroidIR.transmit === 'function') {
            window.AndroidIR.transmit(selectedBrand, commandName);
        } else {
            console.log(`IR Command Dispatched: [${selectedBrand}] - ${commandName}`);
        }
    }

    // Attach click listeners to all remote buttons
    document.querySelectorAll('button').forEach(button => {
        button.addEventListener('click', (e) => {
            const btn = e.currentTarget;
            const command = btn.getAttribute('data-command') || btn.innerText.trim();
            
            // Haptic vibration feedback for mobile devices
            if (navigator.vibrate) {
                navigator.vibrate(40);
            }

            sendIRCommand(command);
        });
    });
});
