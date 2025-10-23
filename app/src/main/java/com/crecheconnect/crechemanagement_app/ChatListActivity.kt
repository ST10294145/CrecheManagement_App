package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatListActivity : AppCompatActivity() {

    private lateinit var recyclerViewChats: RecyclerView
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatList = mutableListOf<String>()

    private val db = FirebaseDatabase.getInstance(
        "https://crechemanagement-app-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        recyclerViewChats = findViewById(R.id.recyclerViewChats)
        recyclerViewChats.layoutManager = LinearLayoutManager(this)

        chatListAdapter = ChatListAdapter(chatList) { chatId ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("receiverId", chatId)
            startActivity(intent)
        }

        recyclerViewChats.adapter = chatListAdapter

        loadChatsRealtime()
    }

    private fun loadChatsRealtime() {
        val userId = auth.currentUser?.uid ?: return
        db.child("chats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (chatSnapshot in snapshot.children) {
                    val participants = chatSnapshot.child("participants").children.map { it.value.toString() }
                    if (participants.contains(userId)) {
                        // Get the other participant's UID
                        val otherId = participants.first { it != userId }
                        chatList.add(otherId)
                    }
                }
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatListActivity, "Failed to load chats", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
