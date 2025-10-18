package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var attendanceAdapter: AttendanceAdapter
    private lateinit var subjectDropdown: MaterialAutoCompleteTextView
    private lateinit var saveButton: Button
    private val attendanceList = mutableListOf<Attendance>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_attendance, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAttendance)
        saveButton = view.findViewById(R.id.btnSave)
        val subjectInputLayout = view.findViewById<TextInputLayout>(R.id.dropdownSubjectLayout)
        subjectDropdown = view.findViewById(R.id.dropdownSubject)

        recyclerView.layoutManager = LinearLayoutManager(context)

        setupSubjectDropdown()
        fetchParentChildren()

        saveButton.setOnClickListener {
            val selectedSubject = subjectDropdown.text.toString()
            if (selectedSubject.isEmpty() || selectedSubject == "Select Subject") {
                Toast.makeText(context, "Please select a subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            attendanceAdapter = AttendanceAdapter(attendanceList, selectedSubject)
            recyclerView.adapter = attendanceAdapter

            saveAttendance(selectedSubject)
        }

        return view
    }

    private fun setupSubjectDropdown() {
        val subjects = listOf("Select Subject", "Math", "English", "Natural Science", "P.E.", "L.O.")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subjects)
        subjectDropdown.setAdapter(adapter)
    }

    private fun fetchParentChildren() {
        db.collection("users")
            .whereEqualTo("role", "parent")
            .get()
            .addOnSuccessListener { result ->
                attendanceList.clear()
                for (doc in result) {
                    val childName = doc.getString("childName") ?: continue
                    attendanceList.add(
                        Attendance(
                            childName = childName,
                            subject = "",
                            isPresent = false,
                            date = null
                        )
                    )
                }
                attendanceAdapter = AttendanceAdapter(attendanceList, "Select Subject")
                recyclerView.adapter = attendanceAdapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching parent accounts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveAttendance(selectedSubject: String) {
        val currentTimestamp = Timestamp.now()

        for (attendance in attendanceList) {
            val record = Attendance(
                childName = attendance.childName,
                subject = selectedSubject,
                isPresent = attendance.isPresent,
                date = currentTimestamp
            )

            db.collection("attendance")
                .add(record)
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error saving: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        Toast.makeText(context, "Attendance saved for $selectedSubject", Toast.LENGTH_LONG).show()
    }
}
