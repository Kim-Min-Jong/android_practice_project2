package com.fc.simplewebbrowser

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    private val webView: WebView by lazy {
        findViewById<WebView>(R.id.webView)
    }
    private val addressBar: EditText by lazy {
        findViewById<EditText>(R.id.addressBar)
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
            loadUrl("https://www.google.com")
        }
    }

    private fun bindViews() {
        addressBar.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                webView.loadUrl(textView.text.toString())
            }
            return@setOnEditorActionListener false
        }
    }
}