package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ChatMessagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatMessagesAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button

    private var chatMessages = mutableListOf<ChatMessage>()
    private var currentUserId = ""
    private var chatId = ""

    private val chatRepo = ChatRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_messages)

        recyclerView = findViewById(R.id.recyclerViewMessages)
        messageInput = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.btnSendMessage)

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        chatId = intent.getStringExtra("chatId") ?: ""

        adapter = ChatMessagesAdapter(chatMessages, currentUserId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Listen to messages
        chatRepo.listenMessages(chatId) { message ->
            chatMessages.add(message)
            adapter.notifyItemInserted(chatMessages.size - 1)
            recyclerView.scrollToPosition(chatMessages.size - 1)
        }

        // Send message
        sendButton.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            chatRepo.sendMessage(chatId, text)
            messageInput.setText("")
        }
    }
}
