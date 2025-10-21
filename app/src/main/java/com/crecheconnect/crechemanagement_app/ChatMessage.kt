package com.crecheconnect.crechemanagement_app

data class ChatMessage(
    val messageId: String = "",
    val senderId: String = "",       // UID of sender
    val receiverId: String = "",     // UID of receiver (parent or admin)
    val messageText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
