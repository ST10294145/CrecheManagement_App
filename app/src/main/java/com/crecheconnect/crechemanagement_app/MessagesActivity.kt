package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessagesActivity : AppCompatActivity() {

    private lateinit var recyclerMessages: RecyclerView
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList = mutableListOf<Message>()

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "MessagesActivity"

    // Get current user email
    private val currentUserEmail: String
        get() = FirebaseAuth.getInstance().currentUser?.email ?: "unknown"

    // Determine if current user is staff (replace with real role check)
    private val isStaff: Boolean
        get() {
            return true // TODO: replace with role check from Users collection
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_messages)

        recyclerMessages = findViewById(R.id.recyclerMessages)
        messagesAdapter = MessagesAdapter(messagesList)
        recyclerMessages.layoutManager = LinearLayoutManager(this)
        recyclerMessages.adapter = messagesAdapter

        findViewById<Button>(R.id.btnSend).setOnClickListener {
            if (isStaff) showMessageDialog()
            else Toast.makeText(this, "Only staff can send messages", Toast.LENGTH_SHORT).show()
        }

        listenForMessages()
    }

    private fun listenForMessages() {
        val receiverCandidates = listOf(currentUserEmail, "all")

        db.collection("messages")
            .whereIn("receiverEmail", receiverCandidates)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val msgs = snapshots.toObjects(Message::class.java)
                    messagesAdapter.submitList(msgs)
                }
            }
    }

    private fun showMessageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_message, null)
        val inputTitle = dialogView.findViewById<EditText>(R.id.inputTitle)
        val inputBody = dialogView.findViewById<EditText>(R.id.inputBody)
        val inputReceiver = dialogView.findViewById<EditText>(R.id.inputReceiver)

        AlertDialog.Builder(this)
            .setTitle("Create Message")
            .setView(dialogView)
            .setPositiveButton("Send") { _, _ ->
                val title = inputTitle.text.toString().trim()
                val body = inputBody.text.toString().trim()
                val receiverEmail = inputReceiver.text.toString().trim()

                if (title.isNotEmpty() && body.isNotEmpty() && receiverEmail.isNotEmpty()) {
                    sendMessageToFirestore(title, body, receiverEmail)
                } else {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendMessageToFirestore(title: String, body: String, receiverEmail: String) {
        val docRef = db.collection("messages").document()
        val message = Message(
            id = docRef.id,
            title = title,
            body = body,
            senderEmail = currentUserEmail,
            receiverEmail = receiverEmail,
            timestamp = System.currentTimeMillis()
        )

        docRef.set(message)
            .addOnSuccessListener {
                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                messagesAdapter.addAtTop(message)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing message", e)
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }
}
