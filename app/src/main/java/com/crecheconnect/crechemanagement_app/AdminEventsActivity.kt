package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminEventsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private val eventList = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        recyclerView = findViewById(R.id.recyclerViewEvents)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter(eventList, "admin")
        recyclerView.adapter = eventAdapter

        fetchEvents()
    }

    private fun fetchEvents() {
        val db = FirebaseFirestore.getInstance()
        db.collection("events")
            .orderBy("dateTime", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                eventList.clear()
                if (result.isEmpty) {
                    Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show()
                }
                for (doc in result) {
                    val event = doc.toObject(Event::class.java)
                    event.id = doc.id
                    eventList.add(event)
                }
                Toast.makeText(this, "Loaded ${eventList.size} events", Toast.LENGTH_SHORT).show()
                eventAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
