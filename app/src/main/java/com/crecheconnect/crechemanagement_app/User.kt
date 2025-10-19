package com.crecheconnect.crechemanagement_app

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "",

    // Parent fields (only used if role == "parent")
    val parentName: String = "",
    val phoneNumber: String = "",
    val address: String = "",

    // Child info
    val childName: String = "",
    val childDob: String = "",
    val childGender: String = "",
    val hasAllergies: String = "",
    val allergyDetails: String = ""

)
