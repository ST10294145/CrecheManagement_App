package com.crecheconnect.crechemanagement_app

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,  // ← NEW: Track if message has been read
    val deliveredAt: Long = 0     // ← NEW: When message was delivered (optional)
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", "", 0, false, 0)
}