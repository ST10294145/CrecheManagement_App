package com.crecheconnect.crechemanagement_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * SIMPLE Message Adapter
 * Just displays messages - no complex features
 */
class MessageAdapter(
    private val messages: List<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            SentViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
            ReceivedViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder) {
            is SentViewHolder -> holder.bind(message)
            is ReceivedViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    // Sent message ViewHolder
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)

        fun bind(message: Message) {
            txtMessage.text = message.message
        }
    }

    // Received message ViewHolder
    class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)

        fun bind(message: Message) {
            txtMessage.text = message.message
        }
    }
}