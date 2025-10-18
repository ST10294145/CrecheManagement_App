package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminAttendanceFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminAttendanceAdapter
    private val attendanceList = mutableListOf<Attendance>()
    private lateinit var subjectDropdown: MaterialAutoCompleteTextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_attendance, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAdminAttendance)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdminAttendanceAdapter(attendanceList)
        recyclerView.adapter = adapter

        val subjectInputLayout = view.findViewById<TextInputLayout>(R.id.dropdownSubjectLayout)
        subjectDropdown = view.findViewById(R.id.dropdownSubject)

        setupSubjectDropdown()
        setupSubjectSelection()

        // Load all attendance by default
        fetchAttendance("All Subjects")

        return view
    }

    private fun setupSubjectDropdown() {
        val subjects = listOf("All Subjects", "Math", "English", "Natural Science", "P.E.", "L.O.")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subjects)
        subjectDropdown.setAdapter(adapter)
    }

    private fun setupSubjectSelection() {
        subjectDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedSubject = subjectDropdown.adapter.getItem(position).toString()
            fetchAttendance(selectedSubject)
        }
    }

    private fun fetchAttendance(subject: String) {
        val query: Query = if (subject == "All Subjects") {
            db.collection("attendance")
        } else {
            db.collection("attendance").whereEqualTo("subject", subject)
        }

        query.get()
            .addOnSuccessListener { result ->
                attendanceList.clear()
                for (doc in result) {
                    val record = doc.toObject(Attendance::class.java)
                    attendanceList.add(record)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching attendance: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
