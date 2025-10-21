package com.crecheconnect.crechemanagement_app

data class Chat(
    val chatId: String,
    val participants: List<String>,
    val lastMessage: String,
    val lastTimestamp: Long  // <- must be Long
)

data class ChatMessage(
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val messageText: String,
    val timestamp: Long  // <- must be Long
)
