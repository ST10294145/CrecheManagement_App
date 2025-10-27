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
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

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
        // Inflate fragment_attendance.xml
        val view = inflater.inflate(R.layout.fragment_attendance, container, false)

        // Initialize UI components from the layout
        recyclerView = view.findViewById(R.id.recyclerViewAttendance)
        saveButton = view.findViewById(R.id.btnSave)
        val subjectInputLayout = view.findViewById<TextInputLayout>(R.id.dropdownSubjectLayout)
        subjectDropdown = view.findViewById(R.id.dropdownSubject)

        recyclerView.layoutManager = LinearLayoutManager(context)

        setupSubjectDropdown()
        fetchParentChildren()

        // When the "Save" button is clicked
        saveButton.setOnClickListener {
            val selectedSubject = subjectDropdown.text.toString()

            // Check if a subject was selected
            if (selectedSubject.isEmpty() || selectedSubject == "Select Subject") {
                Toast.makeText(context, "Please select a subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Initialize adapter with current attendance list and selected subject
            attendanceAdapter = AttendanceAdapter(attendanceList, selectedSubject)
            recyclerView.adapter = attendanceAdapter

            // Save attendance records to Firestore
            saveAttendance(selectedSubject)
        }

        return view
    }

    // Sets up the dropdown list of subjects
    private fun setupSubjectDropdown() {
        // Static list of subjects displayed in the dropdown
        val subjects = listOf("Alphabets", "Nap Time", "Numbers", "Physical Education", "Story Time")
        // Adapter to connect the list to the dropdown view
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subjects)
        subjectDropdown.setAdapter(adapter)
    }

    // Fetches all parents and loads their children into the attendance list
    private fun fetchParentChildren() {
        db.collection("users")
            .whereEqualTo("role", "parent") // Only get documents where role = "parent"
            .get()
            .addOnSuccessListener { result ->
                attendanceList.clear() // Clear any previous data
                for (doc in result) {
                    val childName = doc.getString("childName") ?: continue
                    // Add a new attendance record for each child
                    attendanceList.add(
                        Attendance(
                            childName = childName,
                            subject = "",
                            isPresent = false,
                            date = null
                        )
                    )
                }

                // Initialize the adapter with default "Select Subject" DON'T DELETE!!
                attendanceAdapter = AttendanceAdapter(attendanceList, "Select Subject")
                recyclerView.adapter = attendanceAdapter
            }
            .addOnFailureListener { e ->
                // If something goes wrong, show an error message
                Toast.makeText(context, "Error fetching parent accounts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Saves the attendance records for all children to Firestore
    private fun saveAttendance(selectedSubject: String) {
        val currentDate = com.google.firebase.Timestamp.now() // Current date and time

        // Loop through each attendance record in the list
        for (attendance in attendanceList) {
            val record = Attendance(
                childName = attendance.childName,
                subject = selectedSubject,
                isPresent = attendance.isPresent,
                date = currentDate
            )

            // Add the record to the "attendance" collection in Firestore
            db.collection("attendance")
                .add(record)
                .addOnFailureListener { e ->
                    // If saving fails, show an error
                    Toast.makeText(context, "Error saving: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        Toast.makeText(context, "Attendance saved for $selectedSubject", Toast.LENGTH_LONG).show()
    }
}
