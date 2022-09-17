package com.fc.simplewebbrowser

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    private val webView: WebView by lazy {
        findViewById<WebView>(R.id.webView)
    }
    private val addressBar: EditText by lazy {
        findViewById<EditText>(R.id.addressBar)
    }
    private val goHomeButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.home)
    }
    private val goBackButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.back)
    }
    private val goForwardButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.front)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        bindViews()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }

    private fun bindViews() {
        addressBar.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                webView.loadUrl("https://${textView.text})")
            }
            return@setOnEditorActionListener false
        }
        goBackButton.setOnClickListener {
            webView.goBack()
        }
        goForwardButton.setOnClickListener {
            webView.goForward()
        }
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            // 앱 종료
            super.onBackPressed()
        }
    }

    companion object {
        private const val DEFAULT_URL = "https://www.google.com"
    }
}