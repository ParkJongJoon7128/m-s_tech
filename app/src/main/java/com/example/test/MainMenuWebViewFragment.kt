import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.test.R

// WebViewScreen에 대한 기능 구현 파일
class MainMenuWebViewFragment : Fragment() {
    private lateinit var webView: WebView

    // 앱을 실행하여 WebViewScreen 띄웠을때 레이아웃을 보여주는 기능
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_menu_web_view, container, false)
        webView = view.findViewById(R.id.Webview)
        return view
    }

    // WebView의 세세적 기능 구현
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView.apply {

            loadUrl("http://safewing.co.kr")

            // 자바스크립트 허용
            settings.javaScriptEnabled = true

            // 자바스크립트의 window.open 허용
            settings.javaScriptCanOpenWindowsAutomatically = true

            // 새 창 띄우기 허용
            settings.setSupportMultipleWindows(true)

            // html의 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
            settings.loadWithOverviewMode = true

            // 화면 사이즈 맞추기 허용여부
            settings.useWideViewPort = true

            // 결제 시스템 사용 가능하게 기능 구현
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url == null) return false
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        if (url.startsWith("intent")) {
                            val schemeIntent: Intent = try {
                                Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            } catch (e: Exception) {
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

            // 카카오 주소 api 사용했을시 dialog 안뜨는걸 방지한 코드
            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                    val newWebView = WebView(requireContext()).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }

                    val dialog = Dialog(requireContext()).apply {
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
                    resultMsg?.sendToTarget()
                    return true
                }
            }
        }
    }

    // 핸드폰에서 back button 클릭시 뒤로가기 기능 구현 기능
    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    // 핸드폰에서 back button 클릭시 뒤로가기 기능 구현 파트
    override fun onPause() {
        super.onPause()
        onBackPressedCallback.remove()
    }

    // 핸드폰에서 back button 클릭시 뒤로가기 기능 구현 파트
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }
}
