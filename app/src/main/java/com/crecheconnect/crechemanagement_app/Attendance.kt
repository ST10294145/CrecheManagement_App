package com.crecheconnect.crechemanagement_app

data class Attendance(
    var childName: String = "",
    var subject: String = "",
    var isPresent: Boolean = false,
    var date: String = ""
)
