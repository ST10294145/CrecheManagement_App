package com.crecheconnect.crechemanagement_app

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.URLEncoder

class PayfastWebViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_AMOUNT = "extra_amount"
        const val EXTRA_ITEM_NAME = "extra_item_name"
    }

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payfast_webview)

        webView = findViewById(R.id.payfastWebView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            // For API >= 24
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.url?.let { return handleUrl(it.toString()) }
                return false
            }

            // For older APIs
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { return handleUrl(it) }
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }
        }

        val amount = intent?.getStringExtra(EXTRA_AMOUNT) ?: "100.00"
        val itemName = intent?.getStringExtra(EXTRA_ITEM_NAME) ?: "Creche Payment"

        postToPayfast(amount, itemName)
    }

    private fun postToPayfast(amount: String, itemName: String) {
        val builder = StringBuilder()
        fun add(key: String, value: String) {
            if (builder.isNotEmpty()) builder.append("&")
            builder.append(URLEncoder.encode(key, "UTF-8"))
            builder.append("=")
            builder.append(URLEncoder.encode(value, "UTF-8"))
        }

        add("merchant_id", PayFastConfig.MERCHANT_ID)
        add("merchant_key", PayFastConfig.MERCHANT_KEY)
        add("return_url", PayFastConfig.RETURN_URL)
        add("cancel_url", PayFastConfig.CANCEL_URL)
        add("notify_url", PayFastConfig.NOTIFY_URL)
        add("amount", amount)
        add("item_name", itemName)

        val postData = builder.toString().toByteArray(Charsets.UTF_8)
        webView.postUrl(PayFastConfig.PAYFAST_URL, postData)
    }

    private fun handleUrl(url: String): Boolean {
        if (url.startsWith(PayFastConfig.RETURN_URL, ignoreCase = true)) {
            Toast.makeText(this, "Returned from PayFast. Check server notify for final status.", Toast.LENGTH_LONG).show()
            val uri = Uri.parse(url)
            val pfPaymentId = uri.getQueryParameter("pf_payment_id")
            val intent = intent
            intent.putExtra("payfast_success", true)
            intent.putExtra("pf_payment_id", pfPaymentId)
            setResult(Activity.RESULT_OK, intent)
            finish()
            return true
        } else if (url.startsWith(PayFastConfig.CANCEL_URL, ignoreCase = true)) {
            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show()
            val intent = intent
            intent.putExtra("payfast_success", false)
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
            return true
        }
        return false
    }

    override fun onDestroy() {
        webView.apply {
            clearHistory()
            loadUrl("about:blank")
            removeAllViews()
            destroy()
        }
        super.onDestroy()
    }
}
