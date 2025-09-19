package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ParentAttendanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_attendance)

        // Load ParentAttendanceFragment into this activity
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.parentAttendanceFragmentContainer, ParentAttendanceFragment())
                .commit()
        }
    }
}
