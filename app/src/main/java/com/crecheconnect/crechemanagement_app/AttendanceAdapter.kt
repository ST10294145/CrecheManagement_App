package com.crecheconnect.crechemanagement_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AttendanceAdapter(
    private val attendanceList: List<Attendance>,
    private val selectedSubject: String
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childNameText: TextView = itemView.findViewById(R.id.childNameText)
        val attendanceCheckBox: CheckBox = itemView.findViewById(R.id.attendanceCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendance = attendanceList[position]
        holder.childNameText.text = attendance.childName

        holder.attendanceCheckBox.setOnCheckedChangeListener(null)
        holder.attendanceCheckBox.isChecked = attendance.isPresent

        holder.attendanceCheckBox.setOnCheckedChangeListener { _, isChecked ->
            attendance.isPresent = isChecked
            attendance.subject = selectedSubject
        }
    }

    override fun getItemCount(): Int = attendanceList.size
}
