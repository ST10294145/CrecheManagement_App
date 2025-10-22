package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AdminActivity : AppCompatActivity() {

    private lateinit var btnCreateEvent: Button
    private lateinit var btnRegistration: Button
    private lateinit var btnAttendance: Button
    private lateinit var btnCheckAttendance: Button
    private lateinit var btnEvents: Button
    private lateinit var btnMessages: Button
    private lateinit var btnProfile: ImageView

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

        // Initialize buttons
        btnRegistration = findViewById(R.id.btnRegistration)
        btnAttendance = findViewById(R.id.btnAttendance)
        btnCheckAttendance = findViewById(R.id.btnCheckAttendance)
        btnEvents = findViewById(R.id.btnEvents)
        btnMessages = findViewById(R.id.btnMessages)
        btnProfile = findViewById(R.id.btnProfile)
        btnCreateEvent = findViewById(R.id.btnCreateEvent)

        // Navigation buttons
        btnRegistration.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnAttendance.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        btnCheckAttendance.setOnClickListener {
            startActivity(Intent(this, ViewAttendanceActivity::class.java))
        }

        btnEvents.setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java))
        }

        // Messages button opens ParentListActivity
        btnMessages.setOnClickListener {
            val intent = Intent(this, ParentListActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnCreateEvent.setOnClickListener {
            showCreateEventDialog()
        }
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
                        Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show()
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
