package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ParentListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ParentListAdapter
    private val parentList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_list)

        recyclerView = findViewById(R.id.recyclerViewParents)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ParentListAdapter(parentList) { parent ->
            val chatRepo = ChatRepository()
            chatRepo.createOrGetChat(parent.uid) { chatId ->
                val intent = Intent(this, ChatMessagesActivity::class.java)
                intent.putExtra("chatId", chatId)
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter

        // Fetch all parents from Firestore
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("role", "parent")
            .get()
            .addOnSuccessListener { result ->
                parentList.clear()
                for (doc in result) {
                    val user = User(
                        uid = doc.id,
                        email = doc.getString("email") ?: "",
                        role = doc.getString("role") ?: "parent",
                        parentName = doc.getString("parentName") ?: "",
                        phoneNumber = doc.getString("phoneNumber") ?: "",
                        address = doc.getString("address") ?: "",
                        childName = doc.getString("childName") ?: "",
                        childDob = doc.getString("childDob") ?: "",
                        childGender = doc.getString("childGender") ?: "",
                        hasAllergies = doc.getString("hasAllergies") ?: "",
                        allergyDetails = doc.getString("allergyDetails") ?: ""
                    )
                    parentList.add(user)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load parents", Toast.LENGTH_SHORT).show()
            }
    }
}
