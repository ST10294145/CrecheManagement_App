package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_messages)

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)
        inputMessage = findViewById(R.id.inputMessage)
        btnSendMessage = findViewById(R.id.btnSendMessage)

        adapter = MessageAdapter(messages)
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = adapter

        receiverId = intent.getStringExtra("receiverId") ?: ""
        chatId = getChatId(auth.currentUser?.uid ?: "", receiverId)

        listenForMessages()

        btnSendMessage.setOnClickListener {
            val messageText = inputMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                inputMessage.text.clear()
            }
        }
    }

    private fun getChatId(uid1: String, uid2: String): String =
        if (uid1 < uid2) "$uid1-$uid2" else "$uid2-$uid1"

    private fun sendMessage(messageText: String) {
        val message = Message(
            senderId = auth.currentUser?.uid ?: "",
            receiverId = receiverId,
            message = messageText
        )

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)
    }

    private fun listenForMessages() {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        doc.toObject(Message::class.java)?.let { messages.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                    recyclerViewMessages.scrollToPosition(messages.size - 1)
                }
            }
    }
}
