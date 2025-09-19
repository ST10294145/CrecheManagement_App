package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AttendanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // Load AttendanceFragment into this activity
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AttendanceFragment())
                .commit()
        }
    }
}
