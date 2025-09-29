package com.crecheconnect.crechemanagement_app

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ReceiptGenerator {

    fun generatePdf(context: Context, amount: String?, item: String?, paymentId: String?) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = android.graphics.Paint()
        paint.textSize = 14f

        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        var y = 40
        canvas.drawText("Creche Management Payment Receipt", 10f, y.toFloat(), paint)
        y += 30
        canvas.drawText("Item: $item", 10f, y.toFloat(), paint)
        y += 30
        canvas.drawText("Amount: R $amount", 10f, y.toFloat(), paint)
        y += 30
        canvas.drawText("Payment ID: $paymentId", 10f, y.toFloat(), paint)
        y += 30
        canvas.drawText("Date: $date", 10f, y.toFloat(), paint)

        pdfDocument.finishPage(page)

        try {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (dir != null && !dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, "receipt_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()

            Toast.makeText(context, "Receipt saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving receipt: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
