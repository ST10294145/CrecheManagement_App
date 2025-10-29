package com.crecheconnect.crechemanagement_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
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

        // Common fields
        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val roleSpinner: Spinner = findViewById(R.id.roleSpinner)

        // Role sections
        val parentSection: LinearLayout = findViewById(R.id.parentSection)
        val adminSection: LinearLayout = findViewById(R.id.adminSection)

        // Parent fields
        val parentName: EditText = findViewById(R.id.parentName)
        val parentPhone: EditText = findViewById(R.id.PhoneNumber)
        val parentAddress: EditText = findViewById(R.id.homeAddress)
        val childName: EditText = findViewById(R.id.childFullName)
        val childDob: EditText = findViewById(R.id.childDob)
        val childGender: EditText = findViewById(R.id.childGender)
        val allergySpinner: Spinner = findViewById(R.id.allergySpinner)
        val allergyDetails: EditText = findViewById(R.id.allergyDetails)

        // Admin fields
        val adminName: EditText = findViewById(R.id.adminFullName)
        val adminPhone: EditText = findViewById(R.id.adminPhoneNumber)

        var selectedRole = "parent"

        // Handle role selection
        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                parentSection.visibility = View.GONE
                adminSection.visibility = View.GONE

                selectedRole = when (position) {
                    0 -> {
                        parentSection.visibility = View.VISIBLE
                        "parent"
                    }
                    else -> {
                        adminSection.visibility = View.VISIBLE
                        "staff"
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        allergySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent.getItemAtPosition(position).toString()
                allergyDetails.visibility = if (selected == "Yes") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Automatically insert dashes in childDob (DD-MM-YYYY)
        childDob.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val ddmmyyyy = "DDMMYYYY"
            private val cal = java.util.Calendar.getInstance()

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    var clean = s.toString().replace("[^\\d.]|\\.".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]|\\.".toRegex(), "")

                    val cl = clean.length
                    var sel = cl
                    for (i in 2..cl step 2) {
                        sel++
                    }
                    if (clean == cleanC) sel--

                    if (clean.length < 8) {
                        clean += ddmmyyyy.substring(clean.length)
                    } else {
                        var day = clean.substring(0, 2).toInt()
                        var mon = clean.substring(2, 4).toInt()
                        var year = clean.substring(4, 8).toInt()

                        if (mon > 12) mon = 12
                        cal.set(java.util.Calendar.MONTH, mon - 1)
                        year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                        cal.set(java.util.Calendar.YEAR, year)
                        day = if (day > cal.getActualMaximum(java.util.Calendar.DATE))
                            cal.getActualMaximum(java.util.Calendar.DATE)
                        else day
                        clean = String.format("%02d%02d%04d", day, mon, year)
                    }

                    clean = String.format(
                        "%s-%s-%s",
                        clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8)
                    )

                    sel = if (sel < 0) 0 else sel
                    current = clean
                    childDob.setText(current)
                    childDob.setSelection(if (sel < current.length) sel else current.length)
                }
            }
        })

        // Handle registration
        signUpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentAdmin = auth.currentUser  // Save admin session

            // Create a SECONDARY FirebaseAuth instance to register new user
            val secondaryAuth = FirebaseAuth.getInstance(FirebaseApp.getInstance())

            secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val newUser = task.result?.user
                        val uid = newUser?.uid ?: ""

                        // Base user info
                        val userData = hashMapOf(
                            "uid" to uid,
                            "email" to email,
                            "role" to selectedRole
                        )

                        // Add parent info
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
                                "parentName" to adminName.text.toString().trim(),  // reuse same key for consistency
                                "phoneNumber" to adminPhone.text.toString().trim()
                            )
                            userData.putAll(adminData)
                        }

                        // Save to Firestore
                        db.collection("users").document(uid).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "User registered successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Re-authenticate admin after registering the user
                                secondaryAuth.signOut()
                                if (currentAdmin != null) {
                                    auth.updateCurrentUser(currentAdmin)
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Failed to save user data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
