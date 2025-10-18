package com.crecheconnect.crechemanagement_app

import com.google.firebase.Timestamp

data class Attendance(
    var childName: String = "",
    var subject: String = "",
    var isPresent: Boolean = false,
    var date: Timestamp? = null
)
