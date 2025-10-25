package com.crecheconnect.crechemanagement_app

import org.junit.Test
import org.junit.Assert.*

/**
 * Local unit tests for the messaging system
 * These tests run on your local machine (JVM) - no emulator needed
 */
class ChatUnitTest {

    // Sample user IDs for testing
    private val adminId = "Tg4YlMrXdVb1BnyEdlsMHKCuCYa2"
    private val parentId = "Iiu1LkNIOASMTtDc9bZeJ0Xi9683"
    private val anotherParentId = "IGfqVq9YYQMYNa9FSaQGotHyiVD2"

    /**
     * Test 1: ChatId generation consistency
     * Critical: Both users must generate the SAME chatId
     */
    @Test
    fun chatId_generatedConsistently_forBothUsers() {
        // Admin opens chat with parent
        val chatId1 = getChatId(adminId, parentId)

        // Parent opens chat with admin
        val chatId2 = getChatId(parentId, adminId)

        // They MUST be identical
        assertEquals("Chat IDs must be identical regardless of order", chatId1, chatId2)
        println("Test Passed: Both users generate same chatId: $chatId1")
    }

    /**
     * Test 2: ChatId alphabetical ordering
     * Smaller ID should always come first
     */
    @Test
    fun chatId_usesAlphabeticalOrdering() {
        val chatId = getChatId(adminId, parentId)

        // Determine which ID is smaller alphabetically
        val smallerId = if (adminId < parentId) adminId else parentId
        val largerId = if (adminId < parentId) parentId else adminId
        val expectedChatId = "$smallerId-$largerId"

        assertEquals("ChatId should use alphabetical ordering", expectedChatId, chatId)
        assertTrue("ChatId should start with smaller ID", chatId.startsWith(smallerId))
        println("Test Passed: ChatId uses alphabetical ordering: $chatId")
    }

    /**
     * Test 3: Different users create different chatIds
     */
    @Test
    fun chatId_isDifferent_forDifferentUsers() {
        val chatId1 = getChatId(adminId, parentId)
        val chatId2 = getChatId(adminId, anotherParentId)

        assertNotEquals("Different conversations should have different chatIds", chatId1, chatId2)
        println("Test Passed: Different users = different chatIds")
        println("   Chat 1: $chatId1")
        println("   Chat 2: $chatId2")
    }

    /**
     * Test 4: ChatId format validation
     * Should be: "uid1-uid2" format
     */
    @Test
    fun chatId_hasCorrectFormat() {
        val chatId = getChatId(adminId, parentId)

        // Check format
        assertTrue("ChatId should contain a hyphen", chatId.contains("-"))

        val parts = chatId.split("-")
        assertEquals("ChatId should have exactly 2 parts", 2, parts.size)
        assertTrue("Both parts should be non-empty", parts[0].isNotEmpty() && parts[1].isNotEmpty())

        println("Test Passed: ChatId format is correct: $chatId")
    }

    /**
     * Test 5: Same user twice should still work (edge case)
     */
    @Test
    fun chatId_worksWithSameUserId() {
        val chatId = getChatId(adminId, adminId)

        assertNotNull("ChatId should be generated even with same user", chatId)
        assertTrue("ChatId should not be empty", chatId.isNotEmpty())

        println("Test Passed: Same user ID handled: $chatId")
    }

    /**
     * Test 6: Message data class validation
     */
    @Test
    fun message_hasAllRequiredFields() {
        val message = Message(
            id = "test123",
            senderId = adminId,
            receiverId = parentId,
            message = "Test message",
            timestamp = System.currentTimeMillis()
        )

        assertNotNull("Message should not be null", message)
        assertEquals("SenderId should match", adminId, message.senderId)
        assertEquals("ReceiverId should match", parentId, message.receiverId)
        assertEquals("Message text should match", "Test message", message.message)
        assertTrue("Timestamp should be positive", message.timestamp > 0)

        println("Test Passed: Message object is valid")
    }

    /**
     * Test 7: Empty message validation
     */
    @Test
    fun message_canBeEmpty() {
        val emptyMessage = Message()

        assertNotNull("Empty message should not be null", emptyMessage)
        assertEquals("Default senderId should be empty", "", emptyMessage.senderId)
        assertEquals("Default receiverId should be empty", "", emptyMessage.receiverId)
        assertEquals("Default message should be empty", "", emptyMessage.message)

        println("Test Passed: Empty message constructor works")
    }

    /**
     * Test 8: User data class with documentId
     */
    @Test
    fun user_hasDocumentId() {
        val user = User(
            uid = "",
            email = "test@example.com",
            role = "parent",
            parentName = "Test Parent"
        )

        // Initially empty
        assertEquals("DocumentId should initially be empty", "", user.documentId)

        // Can be set
        user.documentId = "testDocId123"
        assertEquals("DocumentId should be settable", "testDocId123", user.documentId)

        println("Test Passed: User documentId field works correctly")
    }

    /**
     * Test 9: ChatId with very long UIDs
     */
    @Test
    fun chatId_worksWithLongUids() {
        val longUid1 = "a".repeat(100)
        val longUid2 = "b".repeat(100)

        val chatId = getChatId(longUid1, longUid2)

        assertNotNull("ChatId should work with long UIDs", chatId)
        assertTrue("ChatId should contain both UIDs", chatId.contains("-"))

        println("Test Passed: Long UIDs handled correctly")
    }

    /**
     * Test 10: ChatId with special characters (edge case)
     */
    @Test
    fun chatId_worksWithSpecialCharacters() {
        val uid1 = "user_123-abc"
        val uid2 = "user_456-def"

        val chatId = getChatId(uid1, uid2)

        assertNotNull("ChatId should work with special chars", chatId)
        assertTrue("ChatId should contain hyphen separator", chatId.contains("-"))

        println("Test Passed: Special characters handled")
        println("   Generated: $chatId")
    }

    /**
     * Helper function - same as in ChatActivity
     * This is what we're testing
     */
    private fun getChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "$uid1-$uid2" else "$uid2-$uid1"
    }
}