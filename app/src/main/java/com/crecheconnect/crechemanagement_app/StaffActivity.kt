package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class StaffActivity : AppCompatActivity() {

    private lateinit var btnAttendance: Button
    private lateinit var btnEvents: Button
    private lateinit var btnCreateEvent: Button
    private lateinit var btnProfile: ImageView

    private var currentUserRole: String = "staff"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)

        btnProfile = findViewById(R.id.btnProfile)
        btnAttendance = findViewById(R.id.btnAttendance)
        btnEvents = findViewById(R.id.btnEvents)
        btnCreateEvent = findViewById(R.id.btnCreateEvent)

        if (currentUserRole != "staff") btnCreateEvent.visibility = View.GONE

        btnProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        btnAttendance.setOnClickListener { startActivity(Intent(this, AttendanceActivity::class.java)) }
        btnEvents.setOnClickListener { startActivity(Intent(this, EventsActivity::class.java)) }
        btnCreateEvent.setOnClickListener { showCreateEventDialog() }
    }

    private fun showCreateEventDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_event, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val inputTitle = dialogView.findViewById<android.widget.EditText>(R.id.inputTitle)
        val inputDescription = dialogView.findViewById<android.widget.EditText>(R.id.inputDescription)
        val inputLocation = dialogView.findViewById<android.widget.EditText>(R.id.inputLocation)
        val datePicker = dialogView.findViewById<android.widget.DatePicker>(R.id.datePicker)
        val startTimePicker = dialogView.findViewById<android.widget.TimePicker>(R.id.timePicker)
        val endTimePicker = dialogView.findViewById<android.widget.TimePicker>(R.id.timePickerEnd)
        val btnCreate = dialogView.findViewById<Button>(R.id.btnCreate)

        btnCreate.setOnClickListener {
            val title = inputTitle.text.toString().trim()
            val description = inputDescription.text.toString().trim()
            val location = inputLocation.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startCalendar = Calendar.getInstance()
            startCalendar.set(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                startTimePicker.hour,
                startTimePicker.minute,
                0
            )
            val startTimestamp = startCalendar.timeInMillis

            val endCalendar = Calendar.getInstance()
            endCalendar.set(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                endTimePicker.hour,
                endTimePicker.minute,
                0
            )
            val endTimestamp = endCalendar.timeInMillis

            if (endTimestamp <= startTimestamp) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val db = FirebaseFirestore.getInstance()
                val eventId = db.collection("events").document().id

                val event = Event(
                    id = eventId,
                    title = title,
                    description = description,
                    dateTime = startTimestamp,
                    endTime = endTimestamp,
                    location = location
                )

                db.collection("events").document(eventId)
                    .set(event)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
    }
}
