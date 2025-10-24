package com.crecheconnect.crechemanagement_app

import android.app.AlertDialog
import android.content.Intent
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private val events: MutableList<Event>,
    private val userRole: String // "admin" or "staff" or "parent"
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textTitle)
        val description: TextView = itemView.findViewById(R.id.textDescription)
        val dateTime: TextView = itemView.findViewById(R.id.textDateTime)
        val endTime: TextView = itemView.findViewById(R.id.textEndTime)
        val location: TextView = itemView.findViewById(R.id.textLocation)
        val btnAddToCalendar: Button = itemView.findViewById(R.id.btnAddToCalendar)
        val btnDeleteEvent: Button = itemView.findViewById(R.id.btnDeleteEvent)
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

        // Add to Calendar
        holder.btnAddToCalendar.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.title)
                putExtra(CalendarContract.Events.DESCRIPTION, event.description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.dateTime)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endTime)
            }
            it.context.startActivity(intent)
        }

        // Only show Delete button for admin
        holder.btnDeleteEvent.visibility = if (userRole == "admin") View.VISIBLE else View.GONE

        // Delete button logic
        holder.btnDeleteEvent.setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes") { _, _ ->
                    val adapterPos = holder.adapterPosition
                    if (adapterPos != RecyclerView.NO_POSITION) {
                        val db = FirebaseFirestore.getInstance()
                        db.collection("events").document(event.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show()
                                events.removeAt(adapterPos)
                                notifyItemRemoved(adapterPos)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int = events.size
}
