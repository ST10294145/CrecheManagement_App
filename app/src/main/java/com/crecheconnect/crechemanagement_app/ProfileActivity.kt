package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.EmailAuthProvider
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
        val changePasswordButton: Button = findViewById(R.id.btnChangePassword)

        // Load current user info
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role") ?: "N/A"
                        val email = document.getString("email") ?: "N/A"
                        val parentName = document.getString("parentName") ?: "N/A"
                        val phone = document.getString("phoneNumber") ?: "N/A"

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

        // Logout
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Change password button -> show popup dialog
        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun showChangePasswordDialog() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.change_password_dialog, null)

        val etCurrent: EditText = dialogView.findViewById(R.id.etCurrentPassword)
        val etNew: EditText = dialogView.findViewById(R.id.etNewPassword)
        val etConfirm: EditText = dialogView.findViewById(R.id.etConfirmPassword)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Change", null)   // we override later to prevent auto-dismiss
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            val btnChange = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnChange.setOnClickListener {
                val currentPw = etCurrent.text.toString().trim()
                val newPw = etNew.text.toString().trim()
                val confirmPw = etConfirm.text.toString().trim()

                // Basic validation
                if (currentPw.isEmpty()) {
                    etCurrent.error = "Enter current password"
                    etCurrent.requestFocus()
                    return@setOnClickListener
                }
                if (newPw.length < 6) {
                    etNew.error = "Password must be at least 6 characters"
                    etNew.requestFocus()
                    return@setOnClickListener
                }
                if (newPw != confirmPw) {
                    etConfirm.error = "Passwords do not match"
                    etConfirm.requestFocus()
                    return@setOnClickListener
                }

                // Proceed: reauthenticate then update
                val user = auth.currentUser
                val email = user?.email
                if (user == null || email == null) {
                    Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    return@setOnClickListener
                }

                // Reauthenticate with current password
                val credential = EmailAuthProvider.getCredential(email, currentPw)
                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            // Now update password
                            user.updatePassword(newPw)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    } else {
                                        val errMsg = updateTask.exception?.message ?: "Failed to update password"
                                        Toast.makeText(this, "Error: $errMsg", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            val err = reauthTask.exception?.message ?: "Re-authentication failed"
                            // Common cause: wrong current password or recent credential requirement
                            Toast.makeText(this, "Re-authentication failed: $err", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        dialog.show()
    }


    private fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
