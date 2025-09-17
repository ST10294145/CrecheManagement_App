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

    // Replace this with your actual way of getting logged-in user id & role
    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

    // Determine if current user is staff (you must implement actual logic)
    private val isStaff: Boolean
        get() {
            // TODO: replace this with your user-role check (e.g., from your Users collection)
            // For testing you can return true to allow sending
            return true
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

        // Start listening for messages (parents or staff will both get updates if query matches)
        listenForMessages()
    }

    private fun listenForMessages() {
        // For parent devices: listen for messages where receiverId == currentUserId OR receiverId == "all"
        // We'll use whereIn; make a list of the allowed receiverIds
        val receiverCandidates = listOf(currentUserId, "all")

        // Note: whereIn + orderBy may require a composite index. Firestore console will prompt if needed.
        db.collection("messages")
            .whereIn("receiverId", receiverCandidates)
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

        AlertDialog.Builder(this)
            .setTitle("Create Message")
            .setView(dialogView)
            .setPositiveButton("Send") { _, _ ->
                val title = inputTitle.text.toString().trim()
                val body = inputBody.text.toString().trim()
                if (title.isNotEmpty() && body.isNotEmpty()) {
                    sendMessageToFirestore(title, body)
                } else {
                    Toast.makeText(this, "Please enter title and body", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendMessageToFirestore(title: String, body: String) {
        val docRef = db.collection("messages").document()
        val message = Message(
            id = docRef.id,
            title = title,
            body = body,
            senderId = currentUserId,
            receiverId = "all", // change to a specific parent UID if you want to target one parent
            timestamp = System.currentTimeMillis()
        )

        docRef.set(message)
            .addOnSuccessListener {
                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                // The addSnapshotListener will pick this up and update UI for listeners.
                // Optionally add locally for instant feedback:
                messagesAdapter.addAtTop(message)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing message", e)
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }
}
