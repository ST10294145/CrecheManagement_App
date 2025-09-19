package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ParentAttendanceFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var attendanceAdapter: ParentAttendanceAdapter
    private val attendanceList = mutableListOf<Attendance>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parent_attendance, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewParentAttendance)
        recyclerView.layoutManager = LinearLayoutManager(context)
        attendanceAdapter = ParentAttendanceAdapter(attendanceList)
        recyclerView.adapter = attendanceAdapter

        fetchAttendance()

        return view
    }

    private fun fetchAttendance() {
        val db = FirebaseFirestore.getInstance()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        if (currentUserEmail != null) {
            db.collection("attendance")
                .whereEqualTo("parentEmail", currentUserEmail)
                .get()
                .addOnSuccessListener { result ->
                    attendanceList.clear()
                    for (doc in result) {
                        val attendance = doc.toObject(Attendance::class.java)
                        attendanceList.add(attendance)
                    }
                    attendanceAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching attendance: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
