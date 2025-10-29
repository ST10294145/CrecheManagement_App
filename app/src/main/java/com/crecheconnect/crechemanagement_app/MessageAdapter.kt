package com.crecheconnect.crechemanagement_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val messages: List<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // List that includes both messages and date headers
    private val displayItems = mutableListOf<DisplayItem>()

    init {
        processMessagesWithDateHeaders()
    }

    /**
     * Process messages and insert date headers where needed
     */
    private fun processMessagesWithDateHeaders() {
        displayItems.clear()

        var lastDate: String? = null

        messages.forEach { message ->
            val messageDate = getDateString(message.timestamp)

            // If date changed, add a date header
            if (messageDate != lastDate) {
                displayItems.add(DisplayItem.DateHeader(messageDate))
                lastDate = messageDate
            }

            // Add the message
            displayItems.add(DisplayItem.MessageItem(message))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = displayItems[position]) {
            is DisplayItem.DateHeader -> VIEW_TYPE_DATE_HEADER
            is DisplayItem.MessageItem -> {
                if (item.message.senderId == currentUserId) {
                    VIEW_TYPE_SENT
                } else {
                    VIEW_TYPE_RECEIVED
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            VIEW_TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                SentMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = displayItems[position]) {
            is DisplayItem.DateHeader -> {
                (holder as DateHeaderViewHolder).bind(item.date)
            }
            is DisplayItem.MessageItem -> {
                when (holder) {
                    is SentMessageViewHolder -> holder.bind(item.message)
                    is ReceivedMessageViewHolder -> holder.bind(item.message)
                }
            }
        }
    }

    override fun getItemCount(): Int = displayItems.size

    /**
     * Update messages and refresh date headers
     */
    fun updateMessages() {
        processMessagesWithDateHeaders()
        notifyDataSetChanged()
    }

    // ViewHolder for date headers
    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtDate: TextView = itemView.findViewById(R.id.txtDate)

        fun bind(date: String) {
            txtDate.text = date
        }
    }

    // ViewHolder for sent messages
    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
        private val txtTime: TextView = itemView.findViewById(R.id.txtTime)

        fun bind(message: Message) {
            txtMessage.text = message.message
            txtTime.text = formatTime(message.timestamp)
        }
    }

    // ViewHolder for received messages
    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
        private val txtTime: TextView = itemView.findViewById(R.id.txtTime)

        fun bind(message: Message) {
            txtMessage.text = message.message
            txtTime.text = formatTime(message.timestamp)
        }
    }

    /**
     * Sealed class to represent either a message or date header
     */
    sealed class DisplayItem {
        data class MessageItem(val message: Message) : DisplayItem()
        data class DateHeader(val date: String) : DisplayItem()
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
        private const val VIEW_TYPE_DATE_HEADER = 3

        /**
         * Format timestamp as time (e.g., "14:30")
         */
        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        /**
         * Get date string with smart formatting:
         * - Today
         * - Yesterday
         * - Day name (if within last week)
         * - Full date (if older)
         */
        private fun getDateString(timestamp: Long): String {
            val messageDate = Calendar.getInstance().apply {
                timeInMillis = timestamp
            }
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }

            return when {
                // Today
                isSameDay(messageDate, today) -> "Today"

                // Yesterday
                isSameDay(messageDate, yesterday) -> "Yesterday"

                // Within last 7 days - show day name
                isWithinLastWeek(messageDate, today) -> {
                    SimpleDateFormat("EEEE", Locale.getDefault()).format(messageDate.time)
                }

                // Older - show full date
                else -> {
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(messageDate.time)
                }
            }
        }

        /**
         * Check if two dates are the same day
         */
        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }

        /**
         * Check if date is within last 7 days
         */
        private fun isWithinLastWeek(messageDate: Calendar, today: Calendar): Boolean {
            val weekAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -7)
            }
            return messageDate.after(weekAgo) && messageDate.before(today)
        }
    }
}