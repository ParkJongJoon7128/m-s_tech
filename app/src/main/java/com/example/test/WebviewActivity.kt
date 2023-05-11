package com.example.test

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import java.net.URISyntaxException

class WebviewActivity : AppCompatActivity() {

    private lateinit var webview: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webview = findViewById(R.id.Webview)

        webview.apply {
            // 사이트 로드
            this.loadUrl("http://safewing.co.kr")

            // 자바스크립트 허용
            settings.javaScriptEnabled = true

            // 자바스크립트의 window.open 허용
            settings.javaScriptCanOpenWindowsAutomatically = true

            // 새 창 띄우기 허용
            this.settings.setSupportMultipleWindows(true)

            // html의 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
            this.settings.loadWithOverviewMode = true

            // 화면 사이즈 맞추기 허용여부
            this.settings.useWideViewPort = true

            // 웹뷰에서 새 창이 뜨지 않도록 방지하는 구문
            this.webChromeClient = object: WebChromeClient(){
                override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                    val newWebView = WebView(this@WebviewActivity).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }

                    val dialog = Dialog(this@WebviewActivity).apply {
                        setContentView(newWebView)
                        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                        window!!.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
                        show()
                    }

                    newWebView.webChromeClient = object : WebChromeClient() {
                        override fun onCloseWindow(window: WebView?) {
                            dialog.dismiss()
                        }
                    }

                    (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                    resultMsg.sendToTarget()
                    return true
                }
            }

            // 결제 시스템 사용 가능하게 기능 구현
            this.webViewClient = object : WebViewClient() {
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