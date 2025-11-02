package com.crecheconnect.crechemanagement_app

// Redid to make simplier and take out most features that were causing problems
data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", 0)
}