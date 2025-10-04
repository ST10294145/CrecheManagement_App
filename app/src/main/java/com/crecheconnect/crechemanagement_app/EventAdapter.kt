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
        val endTime: TextView = itemView.findViewById(R.id.textEndTime)   // new field
        val location: TextView = itemView.findViewById(R.id.textLocation) // new field
        val btnAddToCalendar: Button = itemView.findViewById(R.id.btnAddToCalendar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

        holder.title.text = event.title
        holder.description.text = event.description
        holder.dateTime.text = "Starts: ${sdf.format(Date(event.dateTime))}"
        holder.endTime.text = "Ends: ${sdf.format(Date(event.endTime))}"
        holder.location.text = "Location: ${event.location}"

        // Add to Calendar button
        holder.btnAddToCalendar.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.title)
                putExtra(CalendarContract.Events.DESCRIPTION, event.description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, event.location) // include location
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.dateTime)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endTime)
            }
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = events.size
}
