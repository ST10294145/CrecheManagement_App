package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatList = mutableListOf<String>() // chat IDs or names

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        recyclerView = findViewById(R.id.recyclerViewChats)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatListAdapter = ChatListAdapter(chatList) { chatId ->
            // Open chat messages activity
            val intent = Intent(this, ChatMessagesActivity::class.java)
            intent.putExtra("chatId", chatId)
            startActivity(intent)
        }
        recyclerView.adapter = chatListAdapter

        loadChats()
    }

    private fun loadChats() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("chats")
            .whereArrayContains("participants", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                chatList.clear()
                for (doc in snapshot.documents) {
                    chatList.add(doc.id)
                }
                chatListAdapter.notifyDataSetChanged()
            }
    }
}
