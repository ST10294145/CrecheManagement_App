package com.crecheconnect.crechemanagement_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message, message.senderId == currentUserId)
    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)

        fun bind(message: Message, isSender: Boolean) {
            txtMessage.text = message.message

            val params = txtMessage.layoutParams as ViewGroup.MarginLayoutParams
            if (isSender) {
                params.marginStart = 100
                params.marginEnd = 0
                txtMessage.setBackgroundResource(R.drawable.bg_message_sent)
            } else {
                params.marginStart = 0
                params.marginEnd = 100
                txtMessage.setBackgroundResource(R.drawable.bg_message_received)
            }
            txtMessage.layoutParams = params
        }
    }
}
