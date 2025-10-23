package com.crecheconnect.crechemanagement_app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ChatRepository {

    // Explicitly use your Realtime Database URL
    private val database = FirebaseDatabase.getInstance(
        "https://crechemanagement-app-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference
    private val auth = FirebaseAuth.getInstance()

    // Create or get chat between admin and parent
    fun createOrGetChat(parentUid: String, onComplete: (chatId: String) -> Unit) {
        val adminUid = auth.currentUser?.uid ?: return
        val chatRef = database.child("chats")

        chatRef.get().addOnSuccessListener { snapshot ->
            var chatIdFound: String? = null

            // Check existing chats for the same participants
            for (child in snapshot.children) {
                val participantsMap = child.child("participants").value as? Map<*, *>
                val participants = participantsMap?.values?.map { it.toString() } ?: emptyList()
                if (participants.contains(adminUid) && participants.contains(parentUid) && participants.size == 2) {
                    chatIdFound = child.key
                    break
                }
            }

            if (chatIdFound != null) {
                onComplete(chatIdFound)
            } else {
                // No chat exists, create a new one
                val newChatId = chatRef.push().key ?: UUID.randomUUID().toString()
                val chat = Chat(
                    chatId = newChatId,
                    participants = listOf(adminUid, parentUid),
                    lastMessage = "",
                    lastTimestamp = System.currentTimeMillis()
                )
                chatRef.child(newChatId).setValue(chat).addOnSuccessListener {
                    onComplete(newChatId)
                }.addOnFailureListener {
                    onComplete("")
                }
            }
        }.addOnFailureListener {
            onComplete("") // return empty string if failed
        }
    }

    // Send a message (atomic update)
    fun sendMessage(chatId: String, messageText: String) {
        val senderUid = auth.currentUser?.uid ?: return
        val participantsRef = database.child("chats").child(chatId).child("participants")

        participantsRef.get().addOnSuccessListener { snapshot ->
            val participants = snapshot.value as? Map<*, *>
            val participantList = participants?.values?.map { it.toString() } ?: emptyList()
            val receiverUid = participantList.firstOrNull { it != senderUid } ?: return@addOnSuccessListener

            val messageId = database.child("chats").child(chatId).child("messages").push().key
                ?: UUID.randomUUID().toString()

            val timestamp = System.currentTimeMillis()
            val message = ChatMessage(
                messageId = messageId,
                senderId = senderUid,
                receiverId = receiverUid,
                messageText = messageText,
                timestamp = timestamp
            )

            // Atomic update: message + lastMessage + lastTimestamp
            val updates = hashMapOf<String, Any>(
                "/chats/$chatId/messages/$messageId" to message,
                "/chats/$chatId/lastMessage" to messageText,
                "/chats/$chatId/lastTimestamp" to timestamp
            )

            database.updateChildren(updates)
        }
    }

    // Listen to messages in real-time
    fun listenMessages(chatId: String, onMessageReceived: (ChatMessage) -> Unit) {
        val messagesRef = database.child("chats").child(chatId).child("messages")
        messagesRef.addChildEventListener(object : com.google.firebase.database.ChildEventListener {
            override fun onChildAdded(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                if (message != null) onMessageReceived(message)
            }

            override fun onChildChanged(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: com.google.firebase.database.DataSnapshot) {}
            override fun onChildMoved(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }
}
