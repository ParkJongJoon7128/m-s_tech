package com.example.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class WebviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webview: WebView = findViewById(R.id.Webview)

        webview.settings.javaScriptEnabled = true // 자바스크립트 허용

        //웹뷰에서 새 창이 뜨지 않도록 방지하는 구문
        webview.webViewClient = WebViewClient()
        webview.webChromeClient = WebChromeClient()

        //링크 주소를 Load 함
        webview.loadUrl("http://175.195.116.3:8219")
    }

    override fun onBackPressed() {
        val webview: WebView = findViewById(R.id.Webview)

        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            super.onBackPressed()
        }
    }
}