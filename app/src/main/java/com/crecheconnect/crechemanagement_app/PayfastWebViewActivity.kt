package com.crecheconnect.crechemanagement_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
    private var merchantReference: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payfast_webview)

        webView = findViewById(R.id.payfastWebView)
        setupWebView()

        val amount = intent.getStringExtra(EXTRA_AMOUNT) ?: "100.00"
        val itemName = intent.getStringExtra(EXTRA_ITEM_NAME) ?: "Creche Payment"

        loadPayfast(amount, itemName)
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // Simple check - if URL contains success, complete the payment
                if (url?.contains("success") == true || url?.contains("complete") == true) {
                    completePayment()
                }
            }
        }
    }

    private fun loadPayfast(amount: String, itemName: String) {
        try {
            // Generate unique reference for tracking
            merchantReference = PayFastConfig.generateMerchantReference()

            val params = mapOf(
                "merchant_id" to PayFastConfig.MERCHANT_ID,
                "merchant_key" to PayFastConfig.MERCHANT_KEY,
                "return_url" to PayFastConfig.RETURN_URL,
                "cancel_url" to PayFastConfig.CANCEL_URL,
                "notify_url" to PayFastConfig.NOTIFY_URL,
                "amount" to amount,
                "item_name" to itemName,
                "email_address" to "test@crecheconnect.com",
                "m_payment_id" to merchantReference,  // ← THIS IS THE KEY FOR TRACKING
                "cell_number" to "0821234567"
            )

            val postData = params.entries.joinToString("&") { (key, value) ->
                "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
            }

            webView.postUrl(PayFastConfig.PAYFAST_URL, postData.toByteArray())

            // Show the reference so you can look for it in PayFast dashboard
            Toast.makeText(this, "Transaction Ref: $merchantReference", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun completePayment() {
        val paymentId = "PF${System.currentTimeMillis()}"
        val resultIntent = Intent().apply {
            putExtra("pf_payment_id", paymentId)
            putExtra("payfast_success", true)
            putExtra("merchant_reference", merchantReference) // Pass back for receipt
        }
        setResult(Activity.RESULT_OK, resultIntent)
        Toast.makeText(this, "Payment successful! Ref: $merchantReference", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}