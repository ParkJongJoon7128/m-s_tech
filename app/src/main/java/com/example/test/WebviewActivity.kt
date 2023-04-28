package com.example.test

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import java.net.URISyntaxException

class WebviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webview: WebView = findViewById(R.id.Webview)

        webview.settings.javaScriptEnabled = true // 자바스크립트 허용

        //웹뷰에서 새 창이 뜨지 않도록 방지하는 구문
        webview.webChromeClient = WebChromeClient()

        //링크 주소를 Load 함
        webview.loadUrl("http://safewing.co.kr")

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    if (url.startsWith("intent")) {
                        val schemeIntent: Intent = try {
                            Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        } catch (e: URISyntaxException) {
                            e.printStackTrace()
                            return false
                        }
                        try {
                            startActivity(schemeIntent)
                            return true
                        } catch (e: ActivityNotFoundException) {
                            val pkgName = schemeIntent.getPackage()
                            if (pkgName != null) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=$pkgName")
                                    )
                                )
                                return true
                            }
                        }
                    } else {
                        return try {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            true
                        } catch (e: Exception) {
                            e.printStackTrace()
                            false
                        }
                    }
                }
                return false
            }
        }
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