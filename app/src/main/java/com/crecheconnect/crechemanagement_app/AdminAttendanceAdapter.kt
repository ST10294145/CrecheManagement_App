package com.crecheconnect.crechemanagement_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminAttendanceAdapter(
    private val attendanceList: List<Attendance>
) : RecyclerView.Adapter<AdminAttendanceAdapter.AttendanceViewHolder>() {

    class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parentEmailText: TextView = itemView.findViewById(R.id.parentEmailText)
        val attendanceDateText: TextView = itemView.findViewById(R.id.attendanceDateText)
        val attendanceStatusText: TextView = itemView.findViewById(R.id.attendanceStatusText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_attendance, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendance = attendanceList[position]
        holder.parentEmailText.text = attendance.parentEmail
        holder.attendanceDateText.text = attendance.date
        holder.attendanceStatusText.text = if (attendance.isPresent) "Present" else "Absent"
    }

    override fun getItemCount(): Int = attendanceList.size
}
