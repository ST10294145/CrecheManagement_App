package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class ParentAttendanceFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var attendanceAdapter: ParentAttendanceAdapter
    private lateinit var subjectDropdown: AutoCompleteTextView
    private val attendanceList = mutableListOf<Attendance>()
    private val db = FirebaseFirestore.getInstance()

    // When the parent is checking their child's attendance for a specific subject
    private val subjects = listOf("Alphabets", "Nap Time", "Numbers", "Physical Education", "Story Time")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parent_attendance, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewParentAttendance)
        subjectDropdown = view.findViewById(R.id.dropdownSubject)
        recyclerView.layoutManager = LinearLayoutManager(context)
        attendanceAdapter = ParentAttendanceAdapter(attendanceList)
        recyclerView.adapter = attendanceAdapter

        setupSubjectDropdown()
        fetchAttendanceForSubject("Select Subject") // Default empty state

        return view
    }

    private fun setupSubjectDropdown() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subjects)
        subjectDropdown.setAdapter(adapter)

        subjectDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedSubject = subjects[position]
            if (selectedSubject != "Select Subject") {
                fetchAttendanceForSubject(selectedSubject)
            } else {
                attendanceList.clear()
                attendanceAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun fetchAttendanceForSubject(subject: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userEmail = currentUser.email ?: return

        db.collection("users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val childName = result.documents[0].getString("childName")

                    if (!childName.isNullOrEmpty()) {
                        // Sorted query by date (oldest → newest)
                        db.collection("attendance")
                            .whereEqualTo("childName", childName)
                            .whereEqualTo("subject", subject)
                            .orderBy("date", Query.Direction.ASCENDING)
                            .get()
                            .addOnSuccessListener { attendanceResult ->
                                attendanceList.clear()
                                for (doc in attendanceResult) {
                                    val attendance = doc.toObject(Attendance::class.java)
                                    attendanceList.add(attendance)
                                }

                                if (attendanceList.isEmpty()) {
                                    Toast.makeText(context, "No attendance found for $subject", Toast.LENGTH_SHORT).show()
                                }

                                attendanceAdapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error loading attendance: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "No child linked to this account.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Parent record not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching child: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
