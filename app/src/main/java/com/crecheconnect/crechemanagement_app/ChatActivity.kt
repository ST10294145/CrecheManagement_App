package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var btnSendMessage: ImageButton
    private lateinit var adapter: MessageAdapter

    private val messages = mutableListOf<Message>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var chatId: String
    private lateinit var receiverId: String
    private var receiverName: String? = null

    companion object {
        private const val TAG = "ChatActivity"

        // Helper function to generate consistent chatId
        fun getChatId(uid1: String, uid2: String): String =
            if (uid1 < uid2) "$uid1-$uid2" else "$uid2-$uid1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_messages)

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)
        inputMessage = findViewById(R.id.inputMessage)
        btnSendMessage = findViewById(R.id.btnSendMessage)

        adapter = MessageAdapter(messages)
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = adapter

        // Get receiver info from intent
        receiverId = intent.getStringExtra("receiverId") ?: ""
        receiverName = intent.getStringExtra("receiverName")

        // Get current user
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null || receiverId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid user data", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Missing user IDs - currentUserId: $currentUserId, receiverId: $receiverId")
            finish()
            return
        }

        // CRITICAL: Generate chatId here to ensure consistency
        // Don't rely on the passed chatId - always regenerate it
        chatId = getChatId(currentUserId, receiverId)

        // Debug logging
        Log.d(TAG, "=== CHAT INITIALIZED ===")
        Log.d(TAG, "Current User ID: $currentUserId")
        Log.d(TAG, "Receiver ID: $receiverId")
        Log.d(TAG, "Generated Chat ID: $chatId")
        Log.d(TAG, "Receiver Name: $receiverName")

        supportActionBar?.title = receiverName ?: "Chat"

        listenForMessages()

        btnSendMessage.setOnClickListener {
            val messageText = inputMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                inputMessage.text.clear()
            }
        }
    }

    private fun sendMessage(messageText: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        val message = Message(
            senderId = currentUserId,
            receiverId = receiverId,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )

        Log.d(TAG, "=== SENDING MESSAGE ===")
        Log.d(TAG, "Chat ID: $chatId")
        Log.d(TAG, "Message: $messageText")
        Log.d(TAG, "From: $currentUserId")
        Log.d(TAG, "To: $receiverId")

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener { docRef ->
                Log.d(TAG, "Message sent successfully: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error sending message", e)
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }

    private fun listenForMessages() {
        Log.d(TAG, "=== LISTENING FOR MESSAGES ===")
        Log.d(TAG, "Chat ID: $chatId")

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to messages", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        doc.toObject(Message::class.java)?.let {
                            messages.add(it)
                            Log.d(TAG, "Loaded message: ${it.message} from ${it.senderId}")
                        }
                    }
                    adapter.notifyDataSetChanged()

                    if (messages.isNotEmpty()) {
                        recyclerViewMessages.scrollToPosition(messages.size - 1)
                    }

                    Log.d(TAG, "Total messages loaded: ${messages.size}")
                }
            }
    }
}