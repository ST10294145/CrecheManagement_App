package com.crecheconnect.crechemanagement_app

data class Message(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = 0L
)


