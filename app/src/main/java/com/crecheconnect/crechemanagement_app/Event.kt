package com.crecheconnect.crechemanagement_app

data class Event(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var dateTime: Long = 0L,
    var endTime: Long = 0L,
    var location: String = ""
)
