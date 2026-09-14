<!-- File Path: app/src/main/AndroidManifest.xml -->
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.nova.universalremote">

    <uses-permission android:name="android.permission.TRANSMIT_IR" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Nova Universal 2"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.AppCompat.Light.NoActionBar">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>


/* File Path: app/src/main/java/com/nova/universalremote/MainActivity.java */
package com.nova.universalremote;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private ConsumerIrManager irManager;

    private static final String HTML_CONTENT = "<!DOCTYPE html>"
            + "<html lang='en'>"
            + "<head>"
            + "<meta charset='UTF-8'>"
            + "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>"
            + "<title>Nova Universal 2</title>"
            + "<style>"
            + "* { box-sizing: border-box; margin: 0; padding: 0; }"
            + "body { font-family: Arial, sans-serif; background-color: #0f172a; color: #fff; height: 100vh; width: 100vw; display: flex; justify-content: center; align-items: center; overflow: hidden; }"
            + ".remote-card { width: 90%; max-width: 380px; height: 95vh; background-color: #1e293b; border-radius: 28px; padding: 20px; display: flex; flex-direction: column; justify-content: space-between; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }"
            + ".top-row { display: flex; justify-content: space-between; align-items: center; }"
            + ".brand { font-size: 14px; letter-spacing: 1px; color: #94a3b8; font-weight: bold; }"
            + ".brand-select { background: #0f172a; color: #38bdf8; border: 1px solid #334155; padding: 4px 8px; border-radius: 12px; font-size: 11px; outline: none; }"
            + ".mode-toggle { background: #0f172a; padding: 3px; border-radius: 20px; display: flex; border: 1px solid #334155; }"
            + ".mode-btn { background: transparent; border: none; color: #64748b; font-size: 10px; font-weight: bold; padding: 4px 10px; border-radius: 15px; cursor: pointer; }"
            + ".mode-btn.active { background: #2563eb; color: #ffffff; }"
            + ".status-bar { background: #0f172a; border-radius: 12px; padding: 8px 14px; display: flex; justify-content: space-between; align-items: center; border: 1px solid #334155; margin-top: 10px; }"
            + ".status-label { font-size: 9px; color: #64748b; text-transform: uppercase; }"
            + ".status-value { font-size: 12px; font-weight: bold; color: #38bdf8; margin-top: 2px; }"
            + ".scan-btn { background: #2563eb; color: #fff; border: none; border-radius: 8px; padding: 4px 8px; font-size: 10px; font-weight: bold; cursor: pointer; }"
            + ".btn { background: #334155; color: #fff; border: none; border-radius: 50%; font-weight: bold; cursor: pointer; display: flex; justify-content: center; align-items: center; }"
            + ".btn:active { background: #475569; transform: scale(0.95); }"
            + ".btn-power { width: 45px; height: 45px; background-color: #ef4444; font-size: 20px; }"
            + ".dpad-section { display: flex; justify-content: center; }"
            + ".dpad { width: 180px; height: 180px; background: #0f172a; border-radius: 50%; display: grid; grid-template-columns: repeat(3, 1fr); grid-template-rows: repeat(3, 1fr); align-items: center; justify-items: center; }"
            + ".dpad-up { grid-column: 2; grid-row: 1; width: 100%; height: 100%; border-radius: 50% 50% 0 0; }"
            + ".dpad-left { grid-column: 1; grid-row: 2; width: 100%; height: 100%; border-radius: 50% 0 0 50%; }"
            + ".dpad-ok { grid-column: 2; grid-row: 2; width: 50px; height: 50px; background: #2563eb; }"
            + ".dpad-right { grid-column: 3; grid-row: 2; width: 100%; height: 100%; border-radius: 0 50% 50% 0; }"
            + ".dpad-down { grid-column: 2; grid-row: 3; width: 100%; height: 100%; border-radius: 0 0 50% 50%; }"
            + ".quick-actions { display: flex; justify-content: space-around; }"
            + ".btn-action { width: 50px; height: 50px; font-size: 18px; }"
            + ".vol-ch-section { display: flex; justify-content: space-around; }"
            + ".pill-control { background: #0f172a; width: 60px; height: 120px; border-radius: 30px; display: flex; flex-direction: column; justify-content: space-between; align-items: center; padding: 5px; font-size: 12px; color: #94a3b8; }"
            + ".pill-control .btn { width: 50px; height: 40px; border-radius: 20px; }"
            + ".app-shortcuts { display: flex; gap: 10px; }"
            + ".app-btn { flex: 1; height: 40px; border-radius: 10px; font-size: 12px; background: #334155; }"
            + "</style>"
            + "</head>"
            + "<body>"
            + "<div class='remote-card'>"
            + "<header>"
            + "<div class='top-row'>"
            + "<span class='brand'>NOVA 2</span>"
            + "<select id='brandSelect' class='brand-select' onchange='selectedBrand = this.value'>"
            + "<option value='walton_vision_china'>Walton / Vision / China</option>"
            + "<option value='samsung'>Samsung Smart TV</option>"
            + "<option value='lg'>LG webOS</option>"
            + "<option value='sony'>Sony Bravia</option>"
            + "<option value='xiaomi'>Xiaomi TV</option>"
            + "</select>"
            + "<div class='mode-toggle'>"
            + "<button class='mode-btn active' id='btnWifi' onclick=\"setMode('wifi')\">Wi-Fi</button>"
            + "<button class='mode-btn' id='btnIr' onclick=\"setMode('ir')\">IR</button>"
            + "</div>"
            + "<button class='btn btn-power' onclick=\"sendCommand('power')\">&#9184;</button>"
            + "</div>"
            + "<div class='status-bar'>"
            + "<div><div class='status-label'>CONNECTED TV</div><div class='status-value' id='displayIp'>SEARCHING...</div></div>"
            + "<button class='scan-btn' onclick='triggerAutoScan()'>AUTO SCAN</button>"
            + "<div><div class='status-label'>STATUS</div><div class='status-value' id='currentStatus'>READY</div></div>"
            + "</div>"
            + "</header>"
            + "<section class='dpad-section'>"
            + "<div class='dpad'>"
            + "<button class='btn dpad-up' onclick=\"sendCommand('up')\">&#9650;</button>"
            + "<button class='btn dpad-left' onclick=\"sendCommand('left')\">&#9664;</button>"
            + "<button class='btn dpad-ok' onclick=\"sendCommand('ok')\">OK</button>"
            + "<button class='btn dpad-right' onclick=\"sendCommand('right')\">&#9654;</button>"
            + "<button class='btn dpad-down' onclick=\"sendCommand('down')\">&#9660;</button>"
            + "</div>"
            + "</section>"
            + "<section class='quick-actions'>"
            + "<button class='btn btn-action' onclick=\"sendCommand('back')\">&#8617;</button>"
            + "<button class='btn btn-action' onclick=\"sendCommand('home')\">&#8962;</button>"
            + "<button class='btn btn-action' onclick=\"sendCommand('menu')\">&#8801;</button>"
            + "<button class='btn btn-action' onclick=\"sendCommand('mute')\">&#128263;</button>"
            + "<button class='btn btn-action' onclick=\"sendCommand('source')\">HDMI</button>"
            + "</section>"
            + "<section class='vol-ch-section'>"
            + "<div class='pill-control'>"
            + "<button class='btn' onclick=\"sendCommand('volUp')\">+</button>"
            + "<span>VOL</span>"
            + "<button class='btn' onclick=\"sendCommand('volDown')\">-</button>"
            + "</div>"
            + "<div class='pill-control'>"
            + "<button class='btn' onclick=\"sendCommand('chUp')\">&#9650;</button>"
            + "<span>CH</span>"
            + "<button class='btn' onclick=\"sendCommand('chDown')\">-</button>"
            + "</div>"
            + "</section>"
            + "<footer class='app-shortcuts'>"
            + "<button class='btn app-btn' onclick=\"sendCommand('youtube')\">YouTube</button>"
            + "<button class='btn app-btn' onclick=\"sendCommand('netflix')\">Netflix</button>"
            + "</footer>"
            + "</div>"
            + "<script>"
            + "let currentMode = 'wifi';"
            + "let selectedBrand = 'walton_vision_china';"
            + "let connectedIp = localStorage.getItem('nova_saved_tv_ip') || '';"
            + "window.onload = function() {"
            + "  if (connectedIp) { document.getElementById('displayIp').innerText = connectedIp; }"
            + "  else { triggerAutoScan(); }"
            + "};"
            + "function triggerAutoScan() {"
            + "  document.getElementById('displayIp').innerText = 'SCANNING...';"
            + "  document.getElementById('currentStatus').innerText = 'BUSY';"
            + "  if (typeof Android !== 'undefined' && Android.discoverSmartTvs) { Android.discoverSmartTvs(); }"
            + "}"
            + "function onTvDiscovered(ip) {"
            + "  connectedIp = ip;"
            + "  localStorage.setItem('nova_saved_tv_ip', ip);"
            + "  document.getElementById('displayIp').innerText = ip;"
            + "  document.getElementById('currentStatus').innerText = 'CONNECTED';"
            + "}"
            + "function onScanFailed() {"
            + "  document.getElementById('displayIp').innerText = connectedIp ? connectedIp : 'NOT FOUND';"
            + "  document.getElementById('currentStatus').innerText = 'READY';"
            + "}"
            + "function setMode(mode) {"
            + "  currentMode = mode;"
            + "  document.getElementById('btnIr').classList.toggle('active', mode === 'ir');"
            + "  document.getElementById('btnWifi').classList.toggle('active', mode === 'wifi');"
            + "  document.getElementById('currentStatus').innerText = mode === 'ir' ? 'IR MODE' : 'READY';"
            + "}"
            + "const BRAND_CODES = {"
            + "  walton_vision_china: { frequency: 38000, power: '0x00FF00FF', volUp: '0x00FF807F', volDown: '0x00FF20DF', chUp: '0x00FF609F', chDown: '0x00FFA05F', ok: '0x00FF22DD', up: '0x00FF02FD', down: '0x00FF9867', left: '0x00FFe01f', right: '0x00FF906F', back: '0x00FF10EF', home: '0x00FFC23D', menu: '0x00FF48B7', mute: '0x00FF08F7', source: '0x00FF40BF' },"
            + "  samsung: { frequency: 38000, power: '0xE0E040BF', volUp: '0xE0E0E0DF', volDown: '0xE0E0D02F', chUp: '0xE0E048B7', chDown: '0xE0E008F7', ok: '0xE0E016E9', up: '0xE0E002FD', down: '0xE0E00CF3', left: '0xE0E0A659', right: '0xE0E046B9', back: '0xE0E01B24', home: '0xE0E09B64', menu: '0xE0E058A7', mute: '0xE0E0F00F', source: '0xE0E0807F' },"
            + "  lg: { frequency: 38000, power: '0x20DF10EF', volUp: '0x20DF40BF', volDown: '0x20DFC03F', chUp: '0x20DF00FF', chDown: '0x20DF807F', ok: '0x20DF22DD', up: '0x20DF02FD', down: '0x20DF827D', left: '0x20DFE01F', right: '0x20DF609F', back: '0x20DF14EB', home: '0x20DF3EC1', menu: '0x20DFC23D', mute: '0x20DF906F', source: '0x20DFD02F' },"
            + "  sony: { frequency: 40000, power: '0xA90', volUp: '0x490', volDown: '0xC90', chUp: '0x090', chDown: '0x890', ok: '0xA70', up: '0x290', down: '0xA90', left: '0x2D0', right: '0xCD0', back: '0xC50', home: '0x070', menu: '0x070', mute: '0x290', source: '0x050' },"
            + "  xiaomi: { frequency: 38000, power: '0x00FF02FD', volUp: '0x00FF00FF', volDown: '0x00FF807F', chUp: '0x00FF40BF', chDown: '0x00FFC03F', ok: '0x00FF20DF', up: '0x00FF10EF', down: '0x00FF906F', left: '0x00FFE01F', right: '0x00FF609F', back: '0x00FFA05F', home: '0x00FF50AF', menu: '0x00FF30CF', mute: '0x00FFD02F', source: '0x00FF708F' }"
            + "};"
            + "function getNecPattern(hexString) {"
            + "  const HEADER_MARK = 9000, HEADER_SPACE = 4500, BIT_MARK = 560, ONE_SPACE = 1690, ZERO_SPACE = 560;"
            + "  let value = parseInt(hexString, 16);"
            + "  let pattern = [HEADER_MARK, HEADER_SPACE];"
            + "  for (let i = 31; i >= 0; i--) {"
            + "    let bit = (value >> i) & 1;"
            + "    pattern.push(BIT_MARK);"
            + "    pattern.push(bit === 1 ? ONE_SPACE : ZERO_SPACE);"
            + "  }"
            + "  pattern.push(BIT_MARK);"
            + "  return pattern;"
            + "}"
            + "function sendCommand(commandName) {"
            + "  if (currentMode === 'ir') {"
            + "    const brand = BRAND_CODES[selectedBrand] || BRAND_CODES.walton_vision_china;"
            + "    const hexCode = brand[commandName];"
            + "    if (hexCode && typeof Android !== 'undefined' && Android.sendIrSignal) {"
            + "      Android.sendIrSignal(brand.frequency, getNecPattern(hexCode).join(','));"
            + "    }"
            + "  } else if (connectedIp) {"
            + "    fetch(`http://${connectedIp}:8080/remote/control?cmd=${commandName}`, { method: 'POST', mode: 'no-cors' });"
            + "  }"
            + "}"
            + "</script>"
            + "</body>"
            + "</html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        irManager = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);

        myWebView = new WebView(this);
        setContentView(myWebView);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        myWebView.setWebViewClient(new WebViewClient());

        myWebView.loadDataWithBaseURL(null, HTML_CONTENT, "text/html", "UTF-8", null);
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void sendIrSignal(int frequency, String patternStr) {
            if (irManager == null || !irManager.hasIrEmitter()) {
                runOnUiThread(() -> Toast.makeText(mContext, "IR Blaster hardware not available!", Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                String[] parts = patternStr.split(",");
                int[] pattern = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    pattern[i] = Integer.parseInt(parts[i].trim());
                }
                irManager.transmit(frequency, pattern);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void discoverSmartTvs() {
            new Thread(() -> {
                try {
                    WifiManager wifi = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiManager.MulticastLock lock = wifi.createMulticastLock("ssdpLock");
                    lock.acquire();

                    String ssdpQuery = "M-SEARCH * HTTP/1.1\r\n" +
                            "HOST: 239.255.255.250:1900\r\n" +
                            "MAN: \"ssdp:discover\"\r\n" +
                            "MX: 2\r\n" +
                            "ST: urn:schemas-upnp-org:device:MediaRenderer:1\r\n\r\n";

                    DatagramSocket socket = new DatagramSocket();
                    socket.setSoTimeout(3000);
                    byte[] sendData = ssdpQuery.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("239.255.255.250"), 1900);
                    socket.send(sendPacket);

                    byte[] recvBuf = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                    socket.receive(receivePacket);

                    String discoveredIp = receivePacket.getAddress().getHostAddress();
                    lock.release();
                    socket.close();

                    runOnUiThread(() -> myWebView.evaluateJavascript("javascript:onTvDiscovered('" + discoveredIp + "')", null));

                } catch (Exception e) {
                    runOnUiThread(() -> myWebView.evaluateJavascript("javascript:onScanFailed()", null));
                }
            }).start();
        }
    }
}
