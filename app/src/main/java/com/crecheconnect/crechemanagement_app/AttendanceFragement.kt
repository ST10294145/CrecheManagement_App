package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val attendanceList = mutableListOf<Attendance>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_attendance, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAttendance)
        recyclerView.layoutManager = LinearLayoutManager(context)
        attendanceAdapter = AttendanceAdapter(attendanceList)
        recyclerView.adapter = attendanceAdapter

        fetchParents()

        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveAttendance()
        }

        return view
    }

    private fun fetchParents() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users") // "users" collection must have email field
            .get()
            .addOnSuccessListener { result ->
                attendanceList.clear()
                for (doc in result) {
                    val email = doc.getString("email") ?: continue
                    attendanceList.add(
                        Attendance(
                            parentEmail = email,
                            isPresent = false,
                            date = "" // blank until saved
                        )
                    )
                }
                attendanceAdapter.notifyDataSetChanged()
                Log.d("AttendanceFragment", "Fetched ${attendanceList.size} parents")
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching parents: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveAttendance() {
        val db = FirebaseFirestore.getInstance()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        for (attendance in attendanceList) {
            // Create a new record for each parent each day
            val record = Attendance(
                parentEmail = attendance.parentEmail,
                isPresent = attendance.isPresent,
                date = currentDate
            )

            db.collection("attendance")
                .add(record) // add a new document, don’t overwrite
                .addOnSuccessListener {
                    Log.d("AttendanceFragment", "Saved attendance for ${attendance.parentEmail}")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to save attendance: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        Toast.makeText(context, "Attendance saved for $currentDate", Toast.LENGTH_SHORT).show()
    }
}
