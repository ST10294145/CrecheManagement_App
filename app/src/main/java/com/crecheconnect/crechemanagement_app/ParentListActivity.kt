package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private val userList = mutableListOf<User>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    companion object {
        private const val TAG = "ParentListActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_list)

        recyclerViewUsers = findViewById(R.id.recyclerViewParents)
        recyclerViewUsers.layoutManager = LinearLayoutManager(this)

        userListAdapter = ParentListAdapter(userList) { user ->
            val currentUserId = auth.currentUser?.uid

            if (currentUserId == null) {
                Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show()
                return@ParentListAdapter
            }

            // CRITICAL FIX: Use documentId (which contains the Firestore document ID)
            val receiverId = user.documentId

            if (receiverId.isEmpty()) {
                Log.e(TAG, "Error: User has no document ID - ${user.parentName}")
                Toast.makeText(this, "Error: Invalid user data", Toast.LENGTH_SHORT).show()
                return@ParentListAdapter
            }

            val chatId = getChatId(currentUserId, receiverId)

            Log.d(TAG, "=== OPENING CHAT ===")
            Log.d(TAG, "Current User ID: $currentUserId")
            Log.d(TAG, "Selected User Name: ${user.parentName.ifEmpty { user.email }}")
            Log.d(TAG, "Selected User ID: $receiverId")
            Log.d(TAG, "Generated Chat ID: $chatId")

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatId", chatId)
            intent.putExtra("receiverId", receiverId)
            intent.putExtra("receiverName", user.parentName.ifEmpty { user.email })
            startActivity(intent)
        }

        recyclerViewUsers.adapter = userListAdapter

        // Admin sees parents; Parent sees admin
        val currentUserRole = intent.getStringExtra("roleFilter") ?: "parent"
        Log.d(TAG, "Loading users with role filter: $currentUserRole")
        loadUsersByRole(currentUserRole)
    }

    private fun loadUsersByRole(role: String) {
        listenerRegistration = db.collection("users")
            .whereEqualTo("role", role)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error loading users", error)
                    Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    userList.clear()
                    for (doc in snapshot.documents) {
                        val user = doc.toObject(User::class.java)
                        if (user != null) {
                            // CRITICAL: Store the Firestore document ID
                            user.documentId = doc.id
                            userList.add(user)

                            Log.d(TAG, "Loaded user: ${user.parentName.ifEmpty { user.email }}, Document ID: ${doc.id}")
                        }
                    }
                    userListAdapter.notifyDataSetChanged()
                    Log.d(TAG, "Total users loaded: ${userList.size}")

                    if (userList.isEmpty()) {
                        Toast.makeText(this, "No users found with role: $role", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun getChatId(uid1: String, uid2: String): String =
        if (uid1 < uid2) "$uid1-$uid2" else "$uid2-$uid1"

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
}