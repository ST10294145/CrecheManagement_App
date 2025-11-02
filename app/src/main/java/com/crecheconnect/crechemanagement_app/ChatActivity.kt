package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var btnSendMessage: ImageButton
    private lateinit var tvChatWith: TextView
    private lateinit var adapter: MessageAdapter

    private val messages = mutableListOf<Message>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var chatId: String
    private lateinit var receiverId: String
    private var receiverName: String? = null

    private var messageListener: ListenerRegistration? = null

    companion object {
        private const val TAG = "ChatActivity"

        // Generate consistent chatId - smaller ID comes first
        fun getChatId(uid1: String, uid2: String): String {
            return if (uid1 < uid2) "$uid1-$uid2" else "$uid2-$uid1"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_messages)

        // Initialize views
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)
        inputMessage = findViewById(R.id.inputMessage)
        btnSendMessage = findViewById(R.id.btnSendMessage)
        tvChatWith = findViewById(R.id.tvChatWith)

        // Setup RecyclerView
        adapter = MessageAdapter(messages, auth.currentUser?.uid ?: "")
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = adapter

        // Get receiver info from intent
        receiverId = intent.getStringExtra("receiverId") ?: ""
        receiverName = intent.getStringExtra("receiverName")

        // Validate
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null || receiverId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid user data", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Invalid user data - currentUserId: $currentUserId, receiverId: $receiverId")
            finish()
            return
        }

        // Generate chatId
        chatId = getChatId(currentUserId, receiverId)

        // Set header
        tvChatWith.text = receiverName ?: "User"

        Log.d(TAG, "Chat initialized - ChatID: $chatId")

        // Load messages
        loadMessages()

        // Send button
        btnSendMessage.setOnClickListener {
            val text = inputMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                inputMessage.text.clear()
            }
        }
    }

    private fun sendMessage(messageText: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        // Create simple message map
        val messageData = hashMapOf(
            "senderId" to currentUserId,
            "receiverId" to receiverId,
            "message" to messageText,
            "timestamp" to System.currentTimeMillis()
        )

        Log.d(TAG, "Sending message to chatId: $chatId")

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(messageData)
            .addOnSuccessListener {
                Log.d(TAG, "✅ Message sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to send message", e)
                Toast.makeText(this, "Failed to send: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadMessages() {
        Log.d(TAG, "Loading messages from chatId: $chatId")

        messageListener = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "❌ Error loading messages", error)
                    Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    messages.clear()

                    for (doc in snapshot.documents) {
                        try {
                            val msg = doc.toObject(Message::class.java)
                            if (msg != null) {
                                messages.add(msg)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing message", e)
                        }
                    }

                    adapter.notifyDataSetChanged()

                    // Scroll to bottom
                    if (messages.isNotEmpty()) {
                        recyclerViewMessages.scrollToPosition(messages.size - 1)
                    }

                    Log.d(TAG, "Loaded ${messages.size} messages")
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        messageListener?.remove()
        Log.d(TAG, "Listener removed")
    }
}