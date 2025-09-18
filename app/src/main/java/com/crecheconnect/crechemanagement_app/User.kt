package com.crecheconnect.crechemanagement_app

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "" // "parent", "staff", "admin"
)
