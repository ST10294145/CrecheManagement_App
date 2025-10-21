package com.crecheconnect.crechemanagement_app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ChatRepository {

    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    // Create or get chat between admin and parent
    fun createOrGetChat(parentUid: String, onComplete: (chatId: String) -> Unit) {
        val adminUid = auth.currentUser?.uid ?: return
        val chatRef = database.child("chats")

        chatRef.orderByChild("participants").get().addOnSuccessListener { snapshot ->
            var chatIdFound: String? = null
            for (child in snapshot.children) {
                val participants = child.child("participants").getValue(List::class.java)
                if (participants?.containsAll(listOf(adminUid, parentUid)) == true) {
                    chatIdFound = child.key
                    break
                }
            }

            if (chatIdFound != null) {
                onComplete(chatIdFound)
            } else {
                val newChatId = chatRef.push().key ?: UUID.randomUUID().toString()
                val chat = Chat(
                    chatId = newChatId,
                    participants = listOf(adminUid, parentUid),
                    lastMessage = "",
                    lastTimestamp = System.currentTimeMillis()
                )
                chatRef.child(newChatId).setValue(chat).addOnSuccessListener {
                    onComplete(newChatId)
                }
            }
        }
    }

    // Send a message
    fun sendMessage(chatId: String, messageText: String) {
        val senderUid = auth.currentUser?.uid ?: return
        val participantsRef = database.child("chats").child(chatId).child("participants")
        participantsRef.get().addOnSuccessListener { snapshot ->
            val participants = snapshot.value as? List<*>
            val receiverUid = participants?.firstOrNull { it != senderUid } as? String ?: return@addOnSuccessListener

            val messageId = database.child("chats").child(chatId).child("messages").push().key
                ?: UUID.randomUUID().toString()

            val message = ChatMessage(
                messageId = messageId,
                senderId = senderUid,
                receiverId = receiverUid,
                messageText = messageText,
                timestamp = System.currentTimeMillis()
            )

            database.child("chats").child(chatId).child("messages").child(messageId).setValue(message)
            database.child("chats").child(chatId).child("lastMessage").setValue(messageText)
            database.child("chats").child(chatId).child("lastTimestamp").setValue(System.currentTimeMillis())
        }
    }

    // Listen to messages
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
