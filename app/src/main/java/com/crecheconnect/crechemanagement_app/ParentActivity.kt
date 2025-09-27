package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.crecheconnect.crechemanagement_app.payment.PaymentActivity

class ParentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_parent)

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Messages Button Logic
        findViewById<Button>(R.id.btnViewMessages).setOnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
        }

        // Profile Image Logic
        findViewById<ImageView>(R.id.ivProfileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Events Button Logic
        findViewById<Button>(R.id.btnViewEvents).setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java))
        }

        // Attendance Button Logic
        findViewById<Button>(R.id.btnAttendance).setOnClickListener {
            startActivity(Intent(this, ParentAttendanceActivity::class.java))
        }

        // Payment Button Logic
        findViewById<Button>(R.id.btnPayments).setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }
    }
}
