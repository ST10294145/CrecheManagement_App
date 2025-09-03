package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    // Firebase Authentication instance (used for logging in users)
    private lateinit var auth: FirebaseAuth

    // Firestore instance (used to fetch extra user details like role)
    private lateinit var db: FirebaseFirestore

    // UI components
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Link UI components from XML layout
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Validate input fields before attempting login
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Attempts to log the user in using Firebase Authentication.
     * If successful, fetches the user’s role from Firestore.
     */
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the logged-in user's unique ID
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        // Fetch the user document from Firestore to get their role
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // Extract the "role" field from Firestore
                                    val role = document.getString("role")
                                    navigateToRoleActivity(role)
                                } else {
                                    Toast.makeText(this, "User role not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                // Log error for debugging and show failure message
                                Log.e("LoginActivity", "Error fetching role: ${e.message}")
                                Toast.makeText(this, "Failed to get user role", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // If authentication fails, display error message
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Directs the user to their appropriate activity based on their role.
     * Roles supported: parent, staff, admin.
     */
    private fun navigateToRoleActivity(role: String?) {
        when (role) {
            "parent" -> startActivity(Intent(this, ParentActivity::class.java))
            "staff" -> startActivity(Intent(this, StaffActivity::class.java))
            "admin" -> startActivity(Intent(this, AdminActivity::class.java))
            else -> Toast.makeText(this, "Invalid role", Toast.LENGTH_SHORT).show()
        }
        // Close the login screen so user cannot go back to it using the back button
        finish()
    }
}
