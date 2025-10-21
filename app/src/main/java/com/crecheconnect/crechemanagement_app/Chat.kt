package com.crecheconnect.crechemanagement_app

data class Chat(
    val chatId: String = "",
    val participants: List<String> = listOf(), // List of UIDs (admin + parent)
    val lastMessage: String = "",
    val lastTimestamp: Long = System.currentTimeMillis()
)
