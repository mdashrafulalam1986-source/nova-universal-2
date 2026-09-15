// Power button handler connecting JS to Android Native Bridge
function onPowerButtonClick() {
    if (typeof AndroidRemote !== "undefined") {
        AndroidRemote.sendPowerSignal();
    } else {
        console.log("Running in browser. IR Blaster trigger disabled.");
    }
}
