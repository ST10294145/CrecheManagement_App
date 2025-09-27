package com.crecheconnect.crechemanagement_app.payment

object PayFastConfig {
    // Sandbox endpoint (development)
    const val PAYFAST_URL = "https://sandbox.payfast.co.za/eng/process"

    // Replace these with your own credentials from PayFast when available.
    // For now use placeholders (or PayFast test merchant credentials if you have them).
    const val MERCHANT_ID = "YOUR_MERCHANT_ID"
    const val MERCHANT_KEY = "YOUR_MERCHANT_KEY"

    // URLs PayFast will redirect to after payment (we will intercept these inside the WebView)
    // Use urls that are easy to detect in WebView, e.g. a domain you control or example values.
    const val RETURN_URL = "https://example.com/payfast-return"
    const val CANCEL_URL = "https://example.com/payfast-cancel"
    // notify_url is for server-to-server verification; set that on PayFast account and implement server endpoint later
    const val NOTIFY_URL = "https://example.com/payfast-notify"
}
