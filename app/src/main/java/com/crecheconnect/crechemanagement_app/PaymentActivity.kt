package com.crecheconnect.crechemanagement_app.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.crecheconnect.crechemanagement_app.R
import com.crecheconnect.crechemanagement_app.PayfastWebViewActivity

class PaymentActivity : AppCompatActivity() {

    private lateinit var amountEditText: EditText
    private lateinit var itemNameEditText: EditText
    private lateinit var payButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment) // your parent activity XML

        amountEditText = findViewById(R.id.amountEditText)
        itemNameEditText = findViewById(R.id.itemNameEditText)
        payButton = findViewById(R.id.payButton)

        payButton.setOnClickListener {
            val amount = amountEditText.text.toString().ifEmpty { "100.00" }
            val itemName = itemNameEditText.text.toString().ifEmpty { "Creche Payment" }

            val intent = Intent(this, PayfastWebViewActivity::class.java).apply {
                putExtra(PayfastWebViewActivity.EXTRA_AMOUNT, amount)
                putExtra(PayfastWebViewActivity.EXTRA_ITEM_NAME, itemName)
            }
            startActivity(intent)
        }
    }
}
