package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatListActivity : AppCompatActivity() {

    private lateinit var recyclerViewChats: RecyclerView
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatList = mutableListOf<ChatItem>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        recyclerViewChats = findViewById(R.id.recyclerViewChats)
        recyclerViewChats.layoutManager = LinearLayoutManager(this)

        chatListAdapter = ChatListAdapter(chatList) { receiverId ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("receiverId", receiverId)
            startActivity(intent)
        }

        recyclerViewChats.adapter = chatListAdapter
        loadChatsRealtime()
    }

    private fun loadChatsRealtime() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("chats").get().addOnSuccessListener { chatsSnapshot ->
            chatList.clear()
            for (chatDoc in chatsSnapshot.documents) {
                val chatId = chatDoc.id
                val participants = chatId.split("-")
                if (!participants.contains(currentUserId)) continue
                val otherId = participants.first { it != currentUserId }

                db.collection("users").document(otherId).get().addOnSuccessListener { userDoc ->
                    val user = userDoc.toObject(User::class.java)
                    val name = user?.parentName ?: "User"
                    chatList.add(ChatItem(otherId, name))
                    chatListAdapter.notifyDataSetChanged()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load chats", Toast.LENGTH_SHORT).show()
        }
    }
}

data class ChatItem(val receiverId: String, val receiverName: String)

