// Power button trigger
function onPowerButtonClick() {
    if (typeof AndroidRemote !== "undefined") {
        AndroidRemote.sendPowerSignal();
    } else {
        console.log("Running in browser. IR Blaster trigger disabled.");
    }
}

// General command handler for TV remote buttons
function onCommand(action) {
    if (typeof AndroidRemote !== "undefined") {
        // Send command to native Android bridge
        AndroidRemote.sendCommand(action);
    } else {
        console.log("Action triggered in browser:", action);
    }
}
