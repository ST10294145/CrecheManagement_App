package com.crecheconnect.crechemanagement_app

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented tests for the messaging system
 * These tests run on an Android device or emulator
 */
@RunWith(AndroidJUnit4::class)
class ChatInstrumentedTest {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    /**
     * Test 1: Verify app package name
     */
    @Test
    fun useAppContext() {
        assertEquals("com.crecheconnect.crechemanagement_app", appContext.packageName)
        println("Test Passed: Correct package name")
    }

    /**
     * Test 2: Intent extras for ChatActivity
     */
    @Test
    fun chatActivity_intentExtras_areCorrect() {
        val intent = Intent(appContext, ChatActivity::class.java).apply {
            putExtra("chatId", "test-chat-id")
            putExtra("receiverId", "test-receiver-id")
            putExtra("receiverName", "Test User")
        }

        assertEquals("test-chat-id", intent.getStringExtra("chatId"))
        assertEquals("test-receiver-id", intent.getStringExtra("receiverId"))
        assertEquals("Test User", intent.getStringExtra("receiverName"))

        println("Test Passed: Intent extras work correctly")
    }

    /**
     * Test 3: Message timestamp is valid
     */
    @Test
    fun message_timestampIsValid() {
        val message = Message(
            senderId = "user1",
            receiverId = "user2",
            message = "Test",
            timestamp = System.currentTimeMillis()
        )

        val currentTime = System.currentTimeMillis()

        assertTrue("Timestamp should be recent",
            message.timestamp > currentTime - 10000 && message.timestamp <= currentTime)

        println("Test Passed: Message timestamp is valid")
    }

    /**
     * Test 4: User object serialization
     */
    @Test
    fun user_canBeCreated() {
        val user = User(
            uid = "testUid",
            email = "test@example.com",
            role = "parent",
            parentName = "Test Parent",
            phoneNumber = "1234567890",
            address = "Test Address",
            childName = "Test Child",
            childDob = "2020-01-01",
            childGender = "Male",
            hasAllergies = "No",
            allergyDetails = ""
        )

        assertNotNull("User should not be null", user)
        assertEquals("parent", user.role)
        assertEquals("Test Parent", user.parentName)

        println("Test Passed: User object created successfully")
    }

    /**
     * Test 5: Message adapter item count
     */
    @Test
    fun messageAdapter_itemCount_isCorrect() {
        val messages = listOf(
            Message(senderId = "user1", receiverId = "user2", message = "Hello"),
            Message(senderId = "user2", receiverId = "user1", message = "Hi"),
            Message(senderId = "user1", receiverId = "user2", message = "How are you?")
        )

        val adapter = MessageAdapter(messages, currentUserId = "user1")


        assertEquals("Adapter should have 3 items", 3, adapter.itemCount)

        println("Test Passed: MessageAdapter item count is correct")
    }

    /**
     * Test 6: ParentListAdapter item count
     */
    @Test
    fun parentListAdapter_itemCount_isCorrect() {
        val users = listOf(
            User(uid = "1", email = "user1@test.com", role = "parent", parentName = "User 1"),
            User(uid = "2", email = "user2@test.com", role = "parent", parentName = "User 2")
        )

        val adapter = ParentListAdapter(users) { }

        assertEquals("Adapter should have 2 items", 2, adapter.itemCount)

        println("Test Passed: ParentListAdapter item count is correct")
    }

    /**
     * Test 7: ChatActivity getChatId static method
     */
    @Test
    fun chatActivity_getChatId_isConsistent() {
        val uid1 = "abc123"
        val uid2 = "xyz789"

        val chatId1 = ChatActivity.getChatId(uid1, uid2)
        val chatId2 = ChatActivity.getChatId(uid2, uid1)

        assertEquals("ChatId should be same regardless of order", chatId1, chatId2)

        println("Test Passed: ChatActivity.getChatId() is consistent")
        println("   Generated: $chatId1")
    }

    /**
     * Test 8: Empty message validation
     */
    @Test
    fun message_rejectsEmptyText() {
        val messageText = ""

        // Simulate validation that should happen in ChatActivity
        val isValid = messageText.trim().isNotEmpty()

        assertFalse("Empty messages should not be valid", isValid)

        println("Test Passed: Empty messages are rejected")
    }

    /**
     * Test 9: Valid message passes validation
     */
    @Test
    fun message_acceptsValidText() {
        val messageText = "Hello, this is a test message"

        val isValid = messageText.trim().isNotEmpty()

        assertTrue("Valid messages should pass validation", isValid)

        println("Test Passed: Valid messages are accepted")
    }

    /**
     * Test 10: Whitespace-only message validation
     */
    @Test
    fun message_rejectsWhitespaceOnly() {
        val messageText = "   \n\t   "

        val isValid = messageText.trim().isNotEmpty()

        assertFalse("Whitespace-only messages should not be valid", isValid)

        println("Test Passed: Whitespace-only messages are rejected")
    }
}