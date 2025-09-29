package com.crecheconnect.crechemanagement_app.payment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.crecheconnect.crechemanagement_app.PayfastWebViewActivity
import com.crecheconnect.crechemanagement_app.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var amountEditText: EditText
    private lateinit var itemNameEditText: EditText
    private lateinit var payButton: Button
    private lateinit var receiptButton: Button

    private var lastPaymentId: String? = null
    private var lastAmount: String? = null
    private var lastItemName: String? = null

    private val payfastResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            lastPaymentId = result.data?.getStringExtra("pf_payment_id")
            lastAmount = amountEditText.text.toString().ifEmpty { "100.00" }
            lastItemName = itemNameEditText.text.toString().ifEmpty { "Creche Payment" }
            Toast.makeText(this, "Payment successful! ID: $lastPaymentId", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Payment canceled or failed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        amountEditText = findViewById(R.id.amountEditText)
        itemNameEditText = findViewById(R.id.itemNameEditText)
        payButton = findViewById(R.id.payButton)
        receiptButton = findViewById(R.id.btnReceipt)

        payButton.setOnClickListener {
            val amount = amountEditText.text.toString().ifEmpty { "100.00" }
            val itemName = itemNameEditText.text.toString().ifEmpty { "Creche Payment" }

            val intent = Intent(this, PayfastWebViewActivity::class.java).apply {
                putExtra(PayfastWebViewActivity.EXTRA_AMOUNT, amount)
                putExtra(PayfastWebViewActivity.EXTRA_ITEM_NAME, itemName)
            }
            payfastResultLauncher.launch(intent)
        }

        receiptButton.setOnClickListener {
            if (lastPaymentId != null) {
                generateReceiptPdf(lastAmount!!, lastItemName!!, lastPaymentId!!)
            } else {
                Toast.makeText(this, "No successful payment yet!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateReceiptPdf(amount: String, itemName: String, paymentId: String) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = android.graphics.Paint()
            paint.textSize = 14f

            val date = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())

            canvas.drawText("Receipt for $itemName", 10f, 25f, paint)
            canvas.drawText("Amount: R$amount", 10f, 50f, paint)
            canvas.drawText("Payment ID: $paymentId", 10f, 75f, paint)
            canvas.drawText("Date: $date", 10f, 100f, paint)
            canvas.drawText("Thank you for your payment!", 10f, 125f, paint)

            pdfDocument.finishPage(page)

            val fileName = "Receipt_${System.currentTimeMillis()}.pdf"
            val outputStream: OutputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    contentResolver.openOutputStream(uri)!!
                } else throw Exception("Failed to create file Uri")
            } else {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                FileOutputStream(file)
            }

            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()

            Toast.makeText(this, "Receipt downloaded to Downloads folder.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error generating receipt: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
