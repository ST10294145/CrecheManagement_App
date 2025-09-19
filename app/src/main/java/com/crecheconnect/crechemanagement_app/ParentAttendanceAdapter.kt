package com.crecheconnect.crechemanagement_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParentAttendanceAdapter(private val attendanceList: List<Attendance>) :
    RecyclerView.Adapter<ParentAttendanceAdapter.ParentAttendanceViewHolder>() {

    class ParentAttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parentEmailText: TextView = itemView.findViewById(R.id.attendanceStatusText)
        val attendanceCheckBox: CheckBox = itemView.findViewById(R.id.attendanceCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentAttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parent_attendance, parent, false)
        return ParentAttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentAttendanceViewHolder, position: Int) {
        val attendance = attendanceList[position]
        holder.parentEmailText.text = attendance.parentEmail
        holder.attendanceCheckBox.isChecked = attendance.isPresent
        holder.attendanceCheckBox.isEnabled = false // Parent cannot edit
    }

    override fun getItemCount(): Int = attendanceList.size
}
