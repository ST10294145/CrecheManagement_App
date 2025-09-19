package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Registration button
        findViewById<Button>(R.id.btnRegistration).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Attendance button
        findViewById<Button>(R.id.btnAttendance).setOnClickListener {
            startActivity(Intent(this, AdminAttendanceActivity::class.java))
        }

        // Events button
        findViewById<Button>(R.id.btnEvents).setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java)) // or AdminEventsActivity if you made it
        }

        // Messages button
        findViewById<Button>(R.id.btnMessages).setOnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
        }

        // Profile picture → ProfileActivity
        findViewById<ImageView>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
