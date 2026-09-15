package com.example.novauniversal;

import android.os.Bundle;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new RemoteBridge(this), "AndroidRemote");
        webView.loadUrl("file:///android_asset/index.html");
    }

    public class RemoteBridge {
        Context mContext;
        ConsumerIrManager irManager;

        RemoteBridge(Context c) {
            mContext = c;
            irManager = (ConsumerIrManager) mContext.getSystemService(Context.CONSUMER_IR_SERVICE);
        }

        @JavascriptInterface
        public void sendPowerSignal() {
            if (irManager != null && irManager.hasIrEmitter()) {
                int frequency = 38000;
                int[] pattern = {9000, 4500, 560, 560, 560, 1690, 560, 1690, 560, 560}; 
                irManager.transmit(frequency, pattern);
                Toast.makeText(mContext, "Signal Sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "No IR Blaster found on this device!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
