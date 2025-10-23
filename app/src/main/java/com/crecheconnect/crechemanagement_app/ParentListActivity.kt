package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ParentListActivity : AppCompatActivity() {

    private lateinit var recyclerViewUsers: RecyclerView
    private lateinit var userListAdapter: ParentListAdapter
    private val userList = mutableListOf<User>() // Your User model

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_list)

        recyclerViewUsers = findViewById(R.id.recyclerViewParents)
        recyclerViewUsers.layoutManager = LinearLayoutManager(this)

        userListAdapter = ParentListAdapter(userList) { user ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("receiverId", user.uid)
            startActivity(intent)
        }

        recyclerViewUsers.adapter = userListAdapter

        // Determine which role to load (admin for parents, parent for admin)
        val roleFilter = intent.getStringExtra("roleFilter") ?: "parent"
        loadUsersByRole(roleFilter)
    }

    private fun loadUsersByRole(role: String) {
        listenerRegistration = db.collection("users")
            .whereEqualTo("role", role)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    userList.clear()
                    for (doc in snapshot.documents) {
                        val user = doc.toObject(User::class.java)
                        user?.let { userList.add(it) }
                    }
                    userListAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
}
