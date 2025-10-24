package com.crecheconnect.crechemanagement_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class EventsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private val eventList = mutableListOf<Event>()

    private var currentUserRole: String = "parent" // Change to "admin" for admin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        recyclerView = findViewById(R.id.recyclerViewEvents)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter(eventList, currentUserRole)
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
                for (doc in result) {
                    val event = doc.toObject(Event::class.java)
                    eventList.add(event)
                }
                eventAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
