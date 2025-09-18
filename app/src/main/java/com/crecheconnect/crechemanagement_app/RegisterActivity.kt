package com.crecheconnect.crechemanagement_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Layout padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Common fields
        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val roleSpinner: Spinner = findViewById(R.id.roleSpinner)

        // Role sections
        val parentSection: LinearLayout = findViewById(R.id.parentSection)
        val staffSection: LinearLayout = findViewById(R.id.staffSection)
        val adminSection: LinearLayout = findViewById(R.id.adminSection)

        var selectedRole = "parent" // default role

        // Handle role changes
        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                parentSection.visibility = View.GONE
                staffSection.visibility = View.GONE
                adminSection.visibility = View.GONE

                selectedRole = when (position) {
                    0 -> {
                        parentSection.visibility = View.VISIBLE
                        "parent"
                    }
                    1 -> {
                        staffSection.visibility = View.VISIBLE
                        "staff"
                    }
                    else -> {
                        adminSection.visibility = View.VISIBLE
                        "admin"
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Handle Sign Up
        signUpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Register user in Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""

                        // Build user object
                        val user = hashMapOf(
                            "uid" to uid,
                            "email" to email,
                            "role" to selectedRole
                        )

                        // Save to Firestore
                        db.collection("users").document(uid).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
