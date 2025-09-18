package com.crecheconnect.crechemanagement_app

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dateTime: Long = 0L,
    val createdBy: String = ""
)
