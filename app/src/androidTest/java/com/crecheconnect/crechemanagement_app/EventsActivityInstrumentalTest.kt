package com.crecheconnect.crechemanagement_app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsActivityInstrumentalTest  {

    @Before
    fun setup() {
        // Optionally, pre-populate Firebase with a test event here
        val db = FirebaseFirestore.getInstance()
        val testEvent = Event(
            id = "test1",
            title = "Test Event",
            description = "This is a test",
            dateTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis() + 3600000,
            location = "Test Location"
        )
        db.collection("events").document(testEvent.id).set(testEvent)
    }

    @Test
    fun recyclerViewDisplaysEvents() {
        ActivityScenario.launch(EventsActivity::class.java)

        // Check if the recycler view is displayed
        onView(withId(R.id.recyclerViewEvents))
            .check(matches(isDisplayed()))

        // Optionally, check if an event title is visible
        onView(withText("Test Event"))
            .check(matches(isDisplayed()))
    }
}
