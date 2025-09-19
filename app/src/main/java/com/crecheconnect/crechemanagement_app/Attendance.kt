package com.crecheconnect.crechemanagement_app

data class Attendance(
    var parentEmail: String = "", // Display this in RecyclerView
    var isPresent: Boolean = false // Attendance status
)
