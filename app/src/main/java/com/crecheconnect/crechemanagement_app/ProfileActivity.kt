package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // === Firebase setup ===
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // === UI references ===
        val tvParentName: TextView = findViewById(R.id.tvParentName)
        val tvRole: TextView = findViewById(R.id.tvRole)
        val tvContact: TextView = findViewById(R.id.tvContact)
        val tvMobile: TextView = findViewById(R.id.tvMobile)
        val logoutButton: Button = findViewById(R.id.btnLogout)

        // === Load current user info ===
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Retrieve stored fields safely
                        val role = document.getString("role") ?: "N/A"
                        val email = document.getString("email") ?: "N/A"
                        val parentName = document.getString("parentName") ?: "N/A"
                        val phone = document.getString("phoneNumber") ?: "N/A"

                        // Set values to text views
                        tvParentName.text = parentName
                        tvRole.text = role.replaceFirstChar { it.uppercase() }
                        tvContact.text = email
                        tvMobile.text = phone
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }

        // === Logout Logic ===
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
