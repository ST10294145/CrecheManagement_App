package com.crecheconnect.crechemanagement_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminAttendanceAdapter(
    private val attendanceList: List<Attendance>
) : RecyclerView.Adapter<AdminAttendanceAdapter.AttendanceViewHolder>() {

    class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childNameText: TextView = itemView.findViewById(R.id.childNameText)
        val subjectText: TextView = itemView.findViewById(R.id.subjectText)
        val attendanceCheckBox: CheckBox = itemView.findViewById(R.id.attendanceCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_attendance, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendance = attendanceList[position]
        holder.childNameText.text = attendance.childName
        holder.subjectText.text = attendance.subject

        // Remove previous listener to avoid recycling issues
        holder.attendanceCheckBox.setOnCheckedChangeListener(null)
        holder.attendanceCheckBox.isChecked = attendance.isPresent

        // Update isPresent when checkbox is toggled
        holder.attendanceCheckBox.setOnCheckedChangeListener { _, isChecked ->
            attendance.isPresent = isChecked
        }
    }

    override fun getItemCount(): Int = attendanceList.size
}
