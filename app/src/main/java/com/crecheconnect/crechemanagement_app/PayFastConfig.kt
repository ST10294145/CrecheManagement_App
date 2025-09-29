package com.crecheconnect.crechemanagement_app

object PayFastConfig {
    // Sandbox credentials
    const val MERCHANT_ID = "10042307"
    const val MERCHANT_KEY = "91ufwzkldlljp"

    // Sandbox URL (switch to https://www.payfast.co.za/eng/process when you go live)
    const val PAYFAST_URL = "https://sandbox.payfast.co.za/eng/process"

    // Temporary redirect URLs (these can be updated to your server later)
    const val RETURN_URL = "https://yourapp.com/return"
    const val CANCEL_URL = "https://yourapp.com/cancel"
    const val NOTIFY_URL = "https://yourapp.com/notify"
}
