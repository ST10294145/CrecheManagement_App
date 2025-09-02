package com.crecheconnect.crechemanagement_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Make sure your root layout in activity_register.xml has android:id="@+id/main"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val roleSpinner: Spinner = findViewById(R.id.roleSpinner)
        val parentSection: LinearLayout = findViewById(R.id.parentSection)
        val staffSection: LinearLayout = findViewById(R.id.staffSection)
        val adminSection: LinearLayout = findViewById(R.id.adminSection)

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

                when (position) {
                    0 -> parentSection.visibility = View.VISIBLE
                    1 -> staffSection.visibility = View.VISIBLE
                    2 -> adminSection.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }
}
