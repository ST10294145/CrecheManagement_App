package com.crecheconnect.crechemanagement_app

data class Message(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val senderEmail: String = "",
    val receiverEmail: String = "",
    val timestamp: Long = 0L
)
