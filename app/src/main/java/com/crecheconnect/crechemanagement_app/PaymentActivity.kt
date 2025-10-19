package com.crecheconnect.crechemanagement_app

import android.content.ContentValues
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private lateinit var addCardButton: Button
    private lateinit var viewCardButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var lastPaymentId: String? = null
    private var lastAmount: String? = null
    private var lastItemName: String? = null
    private var lastMerchantReference: String? = null

    private val payfastResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            lastPaymentId = result.data?.getStringExtra("pf_payment_id")
            lastMerchantReference = result.data?.getStringExtra("merchant_reference")
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
        addCardButton = findViewById(R.id.btnAddCard)
        viewCardButton = findViewById(R.id.btnViewCard)

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

        addCardButton.setOnClickListener {
            showAddCardDialog()
        }

        viewCardButton.setOnClickListener {
            showViewCardDialog()
        }
    }

    // 🪪 Add Card Dialog with validation and auto-slash for expiry date
    private fun showAddCardDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Card")

        val layout = layoutInflater.inflate(R.layout.dialog_add_card, null)
        val cardNumberInput = layout.findViewById<EditText>(R.id.cardNumberInput)
        val cardHolderInput = layout.findViewById<EditText>(R.id.cardHolderInput)
        val expiryInput = layout.findViewById<EditText>(R.id.expiryInput)
        val cvvInput = layout.findViewById<EditText>(R.id.cvvInput)

        // Restrict input to numbers
        cardNumberInput.inputType = InputType.TYPE_CLASS_NUMBER
        cvvInput.inputType = InputType.TYPE_CLASS_NUMBER

        // Automatically add slash for expiry date (MM/YY) without infinite loop
        expiryInput.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                s?.let {
                    val clean = it.toString().replace("/", "")
                    if (clean.length >= 3) {
                        val month = clean.substring(0, 2)
                        val year = clean.substring(2, clean.length)
                        expiryInput.setText("$month/$year")
                        expiryInput.setSelection(expiryInput.text.length)
                    }
                }

                isFormatting = false
            }
        })

        builder.setView(layout)
        builder.setPositiveButton("Save") { _, _ ->
            val cardNumber = cardNumberInput.text.toString().trim()
            val cardHolder = cardHolderInput.text.toString().trim()
            val expiryDate = expiryInput.text.toString().trim()
            val cvv = cvvInput.text.toString().trim()

            // Check for empty fields
            if (cardNumber.isEmpty() || cardHolder.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            // Validate card number length (16 digits)
            if (cardNumber.length != 16) {
                Toast.makeText(this, "Card number must be exactly 16 digits", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            // Validate CVV length (3 digits)
            if (cvv.length != 3) {
                Toast.makeText(this, "CVV must be exactly 3 digits", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val uid = auth.currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val cardData = hashMapOf(
                "cardNumber" to cardNumber,
                "cardHolder" to cardHolder,
                "expiryDate" to expiryDate,
                "cvv" to cvv
            )

            db.collection("parentCards").document(uid)
                .set(cardData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Card saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save card", Toast.LENGTH_SHORT).show()
                }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // 👁️ View/Delete Card Dialog with masking
    private fun showViewCardDialog() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("parentCards").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val cardNumber = document.getString("cardNumber") ?: "N/A"
                    val cardHolder = document.getString("cardHolder") ?: "N/A"
                    val expiryDate = document.getString("expiryDate") ?: "N/A"
                    val cvv = document.getString("cvv") ?: "N/A"

                    val dialogView = layoutInflater.inflate(R.layout.dialog_view_card, null)
                    val cardNumberText = dialogView.findViewById<TextView>(R.id.cardNumberText)
                    val cardHolderText = dialogView.findViewById<TextView>(R.id.cardHolderText)
                    val expiryText = dialogView.findViewById<TextView>(R.id.expiryText)
                    val cvvText = dialogView.findViewById<TextView>(R.id.cvvText)
                    val deleteCardButton = dialogView.findViewById<Button>(R.id.deleteCardButton)

                    // Mask card number and CVV
                    cardNumberText.text = "Card Number: ${maskCardNumber(cardNumber)}"
                    cardHolderText.text = "Card Holder: $cardHolder"
                    expiryText.text = "Expiry Date: $expiryDate"
                    cvvText.text = "CVV: ${maskCVV(cvv)}"

                    val dialog = AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create()

                    deleteCardButton.setOnClickListener {
                        db.collection("parentCards").document(uid).delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Card deleted successfully", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to delete card", Toast.LENGTH_SHORT).show()
                            }
                    }

                    dialog.show()
                } else {
                    Toast.makeText(this, "No saved card found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading card info", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔒 Mask card number
    private fun maskCardNumber(number: String): String {
        return if (number.length >= 4) {
            "**** **** **** ${number.takeLast(4)}"
        } else {
            "****"
        }
    }

    // 🔒 Mask CVV
    private fun maskCVV(cvv: String): String {
        return "***"
    }

    // 📄 Generate Receipt PDF
    private fun generateReceiptPdf(amount: String, itemName: String, paymentId: String) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint()
            paint.textSize = 14f

            val date = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())

            var yPos = 25f
            canvas.drawText("Receipt for $itemName", 10f, yPos, paint)
            yPos += 25f
            canvas.drawText("Amount: R$amount", 10f, yPos, paint)
            yPos += 25f
            canvas.drawText("Payment ID: $paymentId", 10f, yPos, paint)
            yPos += 25f

            if (!lastMerchantReference.isNullOrEmpty()) {
                canvas.drawText("Merchant Ref: $lastMerchantReference", 10f, yPos, paint)
                yPos += 25f
            }

            canvas.drawText("Date: $date", 10f, yPos, paint)
            yPos += 25f
            canvas.drawText("Thank you for your payment!", 10f, yPos, paint)

            pdfDocument.finishPage(page)

            val fileName = "Receipt_${System.currentTimeMillis()}.pdf"
            val outputStream: OutputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
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
