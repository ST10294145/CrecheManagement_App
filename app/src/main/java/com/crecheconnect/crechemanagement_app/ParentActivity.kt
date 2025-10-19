package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.crecheconnect.crechemanagement_app.payment.PaymentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ParentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_parent)

        // Firebase setup
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Find the TextViews
        val tvChildName: TextView = findViewById(R.id.tvChildName)
        val tvChildAge: TextView = findViewById(R.id.tvChildAge)
        val tvChildAllergies: TextView = findViewById(R.id.tvChildAllergies)


        // 🔹 Fetch the logged-in user’s info
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val childName = document.getString("childName") ?: "N/A"
                        val dob = document.getString("childDob") ?: ""

                        // Calculate age if DOB is available
                        val ageText = if (dob.isNotEmpty()) {
                            calculateAge(dob)
                        } else {
                            "Unknown"
                        }
                        val hasAllergies = document.getString("hasAllergies") ?: "No"
                        val allergyDetails = document.getString("allergyDetails") ?: ""

                        tvChildAllergies.text = if (hasAllergies == "Yes" && allergyDetails.isNotEmpty()) {
                            "Allergies: $allergyDetails"
                        } else {
                            "Allergies: None"
                        }


                        tvChildName.text = "Child's Name: $childName"
                        tvChildAge.text = "Age: $ageText"
                    } else {
                        Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load child info", Toast.LENGTH_SHORT).show()
                }
        }

        // 🔹 Buttons (existing logic)
        findViewById<Button>(R.id.btnViewMessages).setOnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
        }

        findViewById<ImageView>(R.id.ivProfileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<Button>(R.id.btnViewEvents).setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java))
        }

        findViewById<Button>(R.id.btnAttendance).setOnClickListener {
            startActivity(Intent(this, ParentAttendanceActivity::class.java))
        }

        findViewById<Button>(R.id.btnPayments).setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }
    }

    // Helper to calculate age from date string (e.g. "2019-05-12")
    private fun calculateAge(dobString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dob = sdf.parse(dobString)
            val today = Calendar.getInstance()
            val birth = Calendar.getInstance()
            birth.time = dob!!

            var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age.toString()
        } catch (e: Exception) {
            "Unknown"
        }
    }
}
