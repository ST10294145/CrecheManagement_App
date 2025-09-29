package com.crecheconnect.crechemanagement_app

object PayFastConfig {
    // Sandbox credentials
    const val MERCHANT_ID = "10042307"
    const val MERCHANT_KEY = "91ufwzkldlljp"

    // Sandbox URL (switch to https://www.payfast.co.za/eng/process when you go live)
    const val PAYFAST_URL = "https://sandbox.payfast.co.za/eng/process"

    // Return and cancel URLs (these can be app-specific deep links)
    const val RETURN_URL = "crecheapp://payfast/return"   // deep link back to your app
    const val CANCEL_URL = "crecheapp://payfast/cancel"   // deep link back to your app

    // Notify URL → this should point to your deployed Firebase HTTPS function
    const val NOTIFY_URL = "https://us-central1-YOUR_PROJECT_ID.cloudfunctions.net/payfastNotify"
}
