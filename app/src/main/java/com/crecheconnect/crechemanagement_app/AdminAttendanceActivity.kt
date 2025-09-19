package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminAttendanceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminAttendanceAdapter
    private val attendanceList = mutableListOf<Attendance>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_attendance)

        recyclerView = findViewById(R.id.recyclerViewAdminAttendance)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminAttendanceAdapter(attendanceList)
        recyclerView.adapter = adapter

        fetchAllAttendance()
    }

    private fun fetchAllAttendance() {
        val db = FirebaseFirestore.getInstance()
        db.collection("attendance")
            .get()
            .addOnSuccessListener { result ->
                attendanceList.clear()
                for (doc in result) {
                    val attendance = doc.toObject(Attendance::class.java)
                    attendanceList.add(attendance)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching attendance: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
