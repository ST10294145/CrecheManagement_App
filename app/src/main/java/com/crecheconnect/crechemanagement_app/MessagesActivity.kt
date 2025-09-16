package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MessagesActivity : AppCompatActivity() {

    private lateinit var recyclerMessages: RecyclerView
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_messages)

        recyclerMessages = findViewById(R.id.recyclerMessages)
        messagesAdapter = MessagesAdapter(messagesList)
        recyclerMessages.layoutManager = LinearLayoutManager(this)
        recyclerMessages.adapter = messagesAdapter

        findViewById<Button>(R.id.btnSend).setOnClickListener {
            showMessageDialog()
        }
    }

    private fun showMessageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_message, null)
        val inputTitle = dialogView.findViewById<EditText>(R.id.inputTitle)
        val inputBody = dialogView.findViewById<EditText>(R.id.inputBody)

        AlertDialog.Builder(this)
            .setTitle("Create Message")
            .setView(dialogView)
            .setPositiveButton("Send") { _, _ ->
                val title = inputTitle.text.toString()
                val body = inputBody.text.toString()
                if (title.isNotEmpty() && body.isNotEmpty()) {
                    sendMessage(Message(title, body))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendMessage(message: Message) {
        messagesList.add(message)
        messagesAdapter.notifyItemInserted(messagesList.size - 1)
    }
}
