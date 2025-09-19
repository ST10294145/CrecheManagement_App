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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Registration button
        val btnRegistration: Button = findViewById(R.id.btnRegistration)
        btnRegistration.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Events button → AdminActivity.kt
        findViewById<Button>(R.id.btnAttendance).setOnClickListener {
            startActivity(Intent(this, AdminEventsActivity::class.java))


        // Profile picture → ProfileActivity
        val profilePic: ImageView = findViewById(R.id.btnProfile)
        profilePic.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }


    }
}
    }
