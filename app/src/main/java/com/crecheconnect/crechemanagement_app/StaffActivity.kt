package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class StaffActivity : AppCompatActivity() {

    private lateinit var btnAttendance: Button
    private lateinit var btnEvents: Button
    private lateinit var btnMessages: Button
    private lateinit var btnProfile: ImageView  // <-- Add this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)

        // Initialize views
        btnProfile = findViewById(R.id.btnProfile)   // <-- Matches XML ID
        btnAttendance = findViewById(R.id.btnAttendance)
        btnEvents = findViewById(R.id.btnEvents)
        btnMessages = findViewById(R.id.btnMessages)


        // Profile picture → open ProfileActivity
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Attendance button → AttendanceActivity
        btnAttendance.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        // Events button → EventsParentActivity
        btnEvents.setOnClickListener {
            startActivity(Intent(this, EventsParentActivity::class.java))
        }

        // Messages button → MessagesActivity
        btnMessages.setOnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
        }

        // Logout button → LoginActivity

    }
}
