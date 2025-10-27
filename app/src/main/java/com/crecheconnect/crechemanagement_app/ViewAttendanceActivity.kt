package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class ViewAttendanceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ParentAttendanceAdapter
    private lateinit var subjectDropdown: MaterialAutoCompleteTextView
    private val attendanceList = mutableListOf<Attendance>()
    private val db = FirebaseFirestore.getInstance()

    // The drop down menu for the admin check attendance
    private val subjects = listOf("Alphabets", "Nap Time", "Numbers", "Physical Education", "Story Time")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_attendance)

        recyclerView = findViewById(R.id.recyclerViewViewAttendance)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ParentAttendanceAdapter(attendanceList)
        recyclerView.adapter = adapter

        subjectDropdown = findViewById(R.id.dropdownSubjectView)
        setupSubjectDropdown()
    }

    private fun setupSubjectDropdown() {
        val adapterDropdown = ArrayAdapter(this, R.layout.dropdown_item, subjects)
        subjectDropdown.setAdapter(adapterDropdown)

        subjectDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedSubject = subjects[position]
            if (selectedSubject != "Select Subject") {
                fetchAttendanceForSubject(selectedSubject)
            } else {
                attendanceList.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun fetchAttendanceForSubject(subject: String) {
        val query: Query = db.collection("attendance")
            .whereEqualTo("subject", subject)
            .orderBy("date", Query.Direction.ASCENDING) // oldest → newest

        query.get()
            .addOnSuccessListener { result ->
                attendanceList.clear()
                for (doc in result) {
                    val attendance = doc.toObject(Attendance::class.java)
                    attendanceList.add(attendance)
                }

                if (attendanceList.isEmpty()) {
                    Toast.makeText(this, "No attendance found for $subject", Toast.LENGTH_SHORT).show()
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch attendance: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
