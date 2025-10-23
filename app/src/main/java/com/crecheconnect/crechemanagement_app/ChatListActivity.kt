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

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatList = mutableListOf<String>() // chat IDs

    private val db = FirebaseDatabase.getInstance(
        "https://crechemanagement-app-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        recyclerView = findViewById(R.id.recyclerViewChats)
        recyclerView.layoutManager = LinearLayoutManager(this)

        chatListAdapter = ChatListAdapter(chatList) { chatId ->
            val intent = Intent(this, ChatMessagesActivity::class.java)
            intent.putExtra("chatId", chatId)
            startActivity(intent)
        }

        recyclerView.adapter = chatListAdapter

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
                        chatList.add(chatSnapshot.key ?: "")
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
