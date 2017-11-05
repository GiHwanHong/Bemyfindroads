package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Created by GiHwan on 2017. 11. 1..
 */

public class AdselectActivity extends Activity {

    private WebView webView;
    private TextView result;
    private Handler handler;
    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adselect);

        result = (TextView) findViewById(R.id.result);

        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();

    }

    public void init_webView() {
        // 순서 //
        // WebView 설정
        // JavaScript 허용
        // JavaScript의 window.open 허용
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        // 두 번째 파라미터는 사용될 php에도 동일하게 사용해야함
        // web client 를 chrome 으로 설정

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new AdselectActivity.AndroidBridge(), "Loads");
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("http://13.124.195.151/getDaum.php");
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    init_webView();
                    result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                    Log.e("arg1 : " , arg1);
                    Log.e("arg2 : " , arg2);
                    Log.e("arg3 : " , arg3);
                    res = String.format("(%s) %s %s", arg1, arg2, arg3);
                    Log.e("res : " , res);

                }
            });
        }
    }

    public void webDatasend(View v) {
        switch (v.getId()) {
            case R.id.sendData:
                Intent in_send = new Intent(AdselectActivity.this, JoinActivity.class);
                in_send.putExtra("address", res);
                Log.e("주소 값을 보낼께 : ", res);
                setResult(RESULT_OK,in_send);
                finish();
                break;
        }
    }

}
