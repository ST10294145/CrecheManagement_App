package com.crecheconnect.crechemanagement_app

data class Attendance(
    var date: String = "", // Displays date
    var parentEmail: String = "", // Display this in RecyclerView
    var isPresent: Boolean = false // Attendance status
)
