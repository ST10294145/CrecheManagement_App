package com.crecheconnect.crechemanagement_app

import java.util.*

object PayFastConfig {
    // Your sandbox credentials
    const val MERCHANT_ID = "10042307"
    const val MERCHANT_KEY = "91ufwzkldlljp"

    // Sandbox URL
    const val PAYFAST_URL = "https://sandbox.payfast.co.za/eng/process"

    // For local testing - these can be dummy URLs since we're monitoring the WebView
    const val RETURN_URL = "https://crecheconnect.com/success"
    const val CANCEL_URL = "https://crecheconnect.com/cancel"
    const val NOTIFY_URL = "https://crecheconnect.com/notify"

    // Generate unique merchant reference for tracking
    fun generateMerchantReference(): String {
        return "CRE${System.currentTimeMillis()}"
    }
}