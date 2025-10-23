package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ParentListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ParentListAdapter
    private val parentList = mutableListOf<User>()

    private val db = FirebaseDatabase.getInstance(
        "https://crechemanagement-app-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference
    private val chatRepo = ChatRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_list)

        recyclerView = findViewById(R.id.recyclerViewParents)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ParentListAdapter(parentList) { parent ->
            if (parent.uid.isEmpty()) {
                Toast.makeText(this, "Invalid parent ID", Toast.LENGTH_SHORT).show()
                return@ParentListAdapter
            }

            chatRepo.createOrGetChat(parent.uid) { chatId ->
                if (chatId.isNotEmpty()) {
                    val intent = Intent(this, ChatMessagesActivity::class.java)
                    intent.putExtra("chatId", chatId)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to open chat", Toast.LENGTH_SHORT).show()
                }
            }
        }

        recyclerView.adapter = adapter

        loadParentsRealtime()
    }

    private fun loadParentsRealtime() {
        db.child("users").orderByChild("role").equalTo("parent")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    parentList.clear()
                    for (parentSnapshot in snapshot.children) {
                        val user = parentSnapshot.getValue(User::class.java)
                        if (user != null) {
                            parentList.add(user)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ParentListActivity, "Failed to load parents", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
