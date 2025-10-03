package com.crecheconnect.crechemanagement_app

import android.content.Intent
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(private val events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textTitle)
        val description: TextView = itemView.findViewById(R.id.textDescription)
        val dateTime: TextView = itemView.findViewById(R.id.textDateTime)
        val btnAddToCalendar: Button = itemView.findViewById(R.id.btnAddToCalendar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.description.text = event.description

        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.dateTime.text = sdf.format(Date(event.dateTime))

        // Add to Calendar button
        holder.btnAddToCalendar.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.title)
                putExtra(CalendarContract.Events.DESCRIPTION, event.description)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.dateTime)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.dateTime + 60 * 60 * 1000) // 1 hour duration
            }
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = events.size
}
