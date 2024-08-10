package com.fallgan.deliveryapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.helper.AppController.Companion.isConnected

class WebViewActivity : AppCompatActivity() {
    private lateinit var mWebView: WebView
    private lateinit var url: String
    lateinit var title: String
    lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra("title").toString()
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        url = intent.getStringExtra("link").toString()
        mWebView = findViewById(R.id.webView1)
        mWebView.settings.javaScriptEnabled = true
        mWebView.settings.setSupportZoom(true)
        mWebView.webViewClient = WebViewClient()
        try {
            if (isConnected(this)) {
                mWebView.loadUrl(url)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        mWebView.onResume()
    }

    @SuppressLint("NewApi")
    override fun onPause() {
        mWebView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}