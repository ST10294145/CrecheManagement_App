package com.crecheconnect.crechemanagement_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // === Common fields ===
        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val roleSpinner: Spinner = findViewById(R.id.roleSpinner)
        val togglePassword: ImageView = findViewById(R.id.togglePassword)

        // === Role sections ===
        val parentSection: LinearLayout = findViewById(R.id.parentSection)
        val adminSection: LinearLayout = findViewById(R.id.adminSection)

        // === Parent fields ===
        val parentName: EditText = findViewById(R.id.parentName)
        val parentPhone: EditText = findViewById(R.id.PhoneNumber)
        val parentAddress: EditText = findViewById(R.id.homeAddress)
        val childName: EditText = findViewById(R.id.childFullName)
        val childDob: EditText = findViewById(R.id.childDob)
        val childGender: EditText = findViewById(R.id.childGender)
        val allergySpinner: Spinner = findViewById(R.id.allergySpinner)
        val allergyDetails: EditText = findViewById(R.id.allergyDetails)

        // === Admin fields ===
        val adminName: EditText = findViewById(R.id.adminFullName)
        val adminPhone: EditText = findViewById(R.id.adminPhoneNumber)

        var selectedRole = "parent"

        // === Role selection ===
        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                parentSection.visibility = View.GONE
                adminSection.visibility = View.GONE

                selectedRole = if (position == 0) {
                    parentSection.visibility = View.VISIBLE
                    "parent"
                } else {
                    adminSection.visibility = View.VISIBLE
                    "staff"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // === Allergy selection ===
        allergySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()
                allergyDetails.visibility = if (selected == "Yes") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // === FIXED DOB TextWatcher (DD-MM-YYYY) ===
        childDob.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val nonDigits = Regex("[^\\d]")
            private val maxLength = 8

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null) return
                val input = s.toString()

                // Avoid infinite loop
                if (input == current) return

                // Remove any non-digit characters
                var clean = input.replace(nonDigits, "")

                // Handle delete case
                if (clean.length < current.replace(nonDigits, "").length) {
                    current = clean
                    return
                }

                // Limit input to 8 digits (DDMMYYYY)
                if (clean.length > maxLength) clean = clean.substring(0, maxLength)

                val formatted = StringBuilder()
                for (i in clean.indices) {
                    formatted.append(clean[i])
                    if (i == 1 || i == 3) formatted.append("-")
                }

                current = formatted.toString()
                childDob.removeTextChangedListener(this)
                childDob.setText(current)
                childDob.setSelection(current.length)
                childDob.addTextChangedListener(this)
            }
        })

        // === Password eye toggle ===
        var isPasswordVisible = false
        togglePassword.setOnClickListener {
            if (isPasswordVisible) {
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePassword.setImageResource(R.drawable.eye)
            } else {
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePassword.setImageResource(R.drawable.eye)
            }
            isPasswordVisible = !isPasswordVisible
            passwordInput.setSelection(passwordInput.text.length)
        }

        // === Registration ===
        signUpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentAdmin = auth.currentUser
            val secondaryAuth = FirebaseAuth.getInstance(FirebaseApp.getInstance())

            secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val newUser = task.result?.user
                        val uid = newUser?.uid ?: ""

                        val userData = hashMapOf(
                            "uid" to uid,
                            "email" to email,
                            "role" to selectedRole
                        )

                        if (selectedRole == "parent") {
                            val parentData = mapOf(
                                "parentName" to parentName.text.toString().trim(),
                                "phoneNumber" to parentPhone.text.toString().trim(),
                                "address" to parentAddress.text.toString().trim(),
                                "childName" to childName.text.toString().trim(),
                                "childDob" to childDob.text.toString().trim(),
                                "childGender" to childGender.text.toString().trim(),
                                "hasAllergies" to allergySpinner.selectedItem.toString(),
                                "allergyDetails" to allergyDetails.text.toString().trim()
                            )
                            userData.putAll(parentData)
                        }

                        if (selectedRole == "admin") {
                            val adminData = mapOf(
                                "parentName" to adminName.text.toString().trim(),
                                "phoneNumber" to adminPhone.text.toString().trim()
                            )
                            userData.putAll(adminData)
                        }

                        db.collection("users").document(uid).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                                secondaryAuth.signOut()
                                currentAdmin?.let { auth.updateCurrentUser(it) }
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
