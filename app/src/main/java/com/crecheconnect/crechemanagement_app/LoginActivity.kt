package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var togglePassword: ImageView
    private lateinit var forgotPasswordText: TextView

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        togglePassword = findViewById(R.id.togglePassword)
        forgotPasswordText = findViewById(R.id.forgotPassword)

        // === LOGIN ===
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // === PASSWORD TOGGLE ===
        togglePassword.setOnClickListener {
            if (isPasswordVisible) {
                passwordInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                isPasswordVisible = false
            } else {
                passwordInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                isPasswordVisible = true
            }
            passwordInput.setSelection(passwordInput.text.length)
        }

        // === FORGOT PASSWORD ===
        forgotPasswordText.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val role = document.getString("role")
                                    navigateToRoleActivity(role)
                                } else {
                                    Toast.makeText(this, "User role not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("LoginActivity", "Error fetching role: ${e.message}")
                                Toast.makeText(this, "Failed to get user role", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToRoleActivity(role: String?) {
        when (role) {
            "parent" -> startActivity(Intent(this, ParentActivity::class.java))
            "staff" -> startActivity(Intent(this, StaffActivity::class.java))
            "admin" -> startActivity(Intent(this, AdminActivity::class.java))
            else -> Toast.makeText(this, "Invalid role", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    // === FORGOT PASSWORD DIALOG ===
    private fun showForgotPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, null)
        val emailInput = dialogView.findViewById<EditText>(R.id.etForgotEmail)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setView(dialogView)
            .setPositiveButton("Send", null)
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            val sendButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            sendButton.setOnClickListener {
                val email = emailInput.text.toString().trim()

                if (email.isEmpty()) {
                    emailInput.error = "Enter your email address"
                    return@setOnClickListener
                }

                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Password reset email sent to $email",
                                Toast.LENGTH_LONG
                            ).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to send reset email: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        dialog.show()
    }
}
