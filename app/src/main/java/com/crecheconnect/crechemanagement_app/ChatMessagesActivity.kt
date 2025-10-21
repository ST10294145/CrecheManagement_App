package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class ChatMessagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatMessagesAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var chatMessages = mutableListOf<ChatMessage>()
    private var currentUserId = ""
    private var receiverId = ""  // The person you are chatting with
    private var receiverName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_messages)

        // Initialize
        recyclerView = findViewById(R.id.recyclerViewMessages)
        messageInput = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.btnSendMessage)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        // Get receiver info from Intent
        receiverId = intent.getStringExtra("receiverId") ?: ""
        receiverName = intent.getStringExtra("receiverName") ?: ""

        adapter = ChatMessagesAdapter(chatMessages, currentUserId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load messages in real-time
        db.collection("messages")
            .whereIn("senderId", listOf(currentUserId, receiverId))
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                chatMessages.clear()
                snapshot?.documents?.forEach { doc ->
                    val message = doc.toObject(ChatMessage::class.java)
                    if (message != null) {
                        // Only show messages between these two users
                        if ((message.senderId == currentUserId && message.receiverId == receiverId) ||
                            (message.senderId == receiverId && message.receiverId == currentUserId)) {
                            chatMessages.add(message)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(chatMessages.size - 1)
            }

        // Send button
        sendButton.setOnClickListener {
            val msgText = messageInput.text.toString().trim()
            if (msgText.isEmpty()) return@setOnClickListener

            val newMessageRef = db.collection("messages").document()
            val message = ChatMessage(
                id = newMessageRef.id,
                senderId = currentUserId,
                senderName = auth.currentUser?.displayName ?: "Unknown",
                receiverId = receiverId,
                message = msgText,
                timestamp = System.currentTimeMillis()
            )
            newMessageRef.set(message)
                .addOnSuccessListener {
                    messageInput.setText("")
                }
        }
    }
}
