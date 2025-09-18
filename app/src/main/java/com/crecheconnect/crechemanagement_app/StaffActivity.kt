package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StaffActivity : AppCompatActivity() {

    private lateinit var btnAttendance: Button
    private lateinit var btnEvents: Button
    private lateinit var btnMessages: Button
    private lateinit var btnRegistration: Button // keep for completeness

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff) // links to your XML

        // Initialize buttons
        btnAttendance = findViewById(R.id.btnAttendance)
        btnEvents = findViewById(R.id.btnEvents)
        btnMessages = findViewById(R.id.btnMessages)

        // Attendance button → AttendanceActivity
        btnAttendance.setOnClickListener {
            val intent = Intent(this, AttendanceActivity::class.java)
            startActivity(intent)
        }

        // Events button → EventsParentActivity
        btnEvents.setOnClickListener {
            val intent = Intent(this, EventsParentActivity::class.java)
            startActivity(intent)
        }

        // Messages button → MessagesActivity
        btnMessages.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

    }
}
