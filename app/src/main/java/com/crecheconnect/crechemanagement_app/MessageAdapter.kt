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
    private val displayItems = mutableListOf<DisplayItem>()

    init {
        processMessagesWithDateHeaders()
    }

    private fun processMessagesWithDateHeaders() {
        displayItems.clear()
        var lastDate: String? = null

        messages.forEach { message ->
            val messageDate = getDateString(message.timestamp)
            if (messageDate != lastDate) {
                displayItems.add(DisplayItem.DateHeader(messageDate))
                lastDate = messageDate
            }
            displayItems.add(DisplayItem.MessageItem(message))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = displayItems[position]) {
            is DisplayItem.DateHeader -> VIEW_TYPE_DATE_HEADER
            is DisplayItem.MessageItem -> if (item.message.senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE_HEADER -> DateHeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false))
            VIEW_TYPE_SENT -> SentMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false))
            else -> ReceivedMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = displayItems[position]) {
            is DisplayItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item.date)
            is DisplayItem.MessageItem -> when (holder) {
                is SentMessageViewHolder -> holder.bind(item.message)
                is ReceivedMessageViewHolder -> holder.bind(item.message)
            }
        }
    }

    override fun getItemCount(): Int = displayItems.size

    fun updateMessages() {
        processMessagesWithDateHeaders()
        notifyDataSetChanged()
    }

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        fun bind(date: String) { txtDate.text = date }
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
        private val txtTime: TextView = itemView.findViewById(R.id.txtTime)
        private val txtReadStatus: TextView = itemView.findViewById(R.id.txtReadStatus)

        fun bind(message: Message) {
            txtMessage.text = message.message
            txtTime.text = formatTime(message.timestamp)

            txtReadStatus.text = when {
                message.isRead -> "✓✓"
                message.deliveredAt > 0 -> "✓✓"
                else -> "✓"
            }
            txtReadStatus.setTextColor(
                when {
                    message.isRead -> 0xFF2196F3.toInt()
                    else -> 0xFF666666.toInt()
                }
            )
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
        private val txtTime: TextView = itemView.findViewById(R.id.txtTime)

        fun bind(message: Message) {
            txtMessage.text = message.message
            txtTime.text = formatTime(message.timestamp)
        }
    }

    sealed class DisplayItem {
        data class MessageItem(val message: Message) : DisplayItem()
        data class DateHeader(val date: String) : DisplayItem()
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
        private const val VIEW_TYPE_DATE_HEADER = 3

        private fun formatTime(timestamp: Long): String =
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))

        private fun getDateString(timestamp: Long): String {
            val messageDate = Calendar.getInstance().apply { timeInMillis = timestamp }
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

            return when {
                isSameDay(messageDate, today) -> "Today"
                isSameDay(messageDate, yesterday) -> "Yesterday"
                isWithinLastWeek(messageDate, today) -> SimpleDateFormat("EEEE", Locale.getDefault()).format(messageDate.time)
                else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(messageDate.time)
            }
        }

        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean =
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)

        private fun isWithinLastWeek(messageDate: Calendar, today: Calendar): Boolean {
            val weekAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
            return messageDate.after(weekAgo) && messageDate.before(today)
        }
    }
}
