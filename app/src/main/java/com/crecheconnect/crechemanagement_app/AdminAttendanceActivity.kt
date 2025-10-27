package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AdminAttendanceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminAttendanceAdapter
    private lateinit var spinnerSubjects: Spinner
    private lateinit var btnSave: Button
    private val attendanceList = mutableListOf<Attendance>()
    private val subjects = listOf("Alphabets", "Nap Time", "Numbers", "Physical Education", "Story Time")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_attendance)

        spinnerSubjects = findViewById(R.id.spinnerSubjects)
        recyclerView = findViewById(R.id.recyclerViewAdminAttendance)
        btnSave = findViewById(R.id.btnSaveAttendance)

        spinnerSubjects.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subjects)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminAttendanceAdapter(attendanceList)
        recyclerView.adapter = adapter

        fetchAllChildren()

        btnSave.setOnClickListener {
            saveAttendance()
        }
    }

    private fun fetchAllChildren() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("role", "parent")
            .get()
            .addOnSuccessListener { result ->
                attendanceList.clear()
                for (doc in result) {
                    val childName = doc.getString("childName") ?: "Unknown"
                    attendanceList.add(Attendance(childName = childName, isPresent = false))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching children: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveAttendance() {
        val db = FirebaseFirestore.getInstance()
        val subject = spinnerSubjects.selectedItem.toString()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        for (attendance in attendanceList) {
            val data = hashMapOf(
                "childName" to attendance.childName,
                "subject" to subject,
                "isPresent" to attendance.isPresent,
                "date" to currentDate
            )

            db.collection("attendance")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Saved: ${attendance.childName}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving attendance: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
