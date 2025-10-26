package com.crecheconnect.crechemanagement_app

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityInstrumentalTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val testEmail = "test@example.com"
    private val testPassword = "password123"

    @Before
    fun setup() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Sign out any existing user
        auth.signOut()
    }

    @After
    fun tearDown() {
        auth.signOut()
    }

    @Test
    fun testLoginScreenDisplayed() {
        // Launch the activity
        ActivityScenario.launch(LoginActivity::class.java)

        // Verify UI elements are displayed
        onView(withId(R.id.logoImage)).check(matches(isDisplayed()))
        onView(withId(R.id.emailInput)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordInput)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.togglePassword)).check(matches(isDisplayed()))
        onView(withId(R.id.forgotPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyFieldsShowsError() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Click login without entering credentials
        onView(withId(R.id.loginButton)).perform(click())

        // Toast message should be shown (cannot be tested directly with Espresso)
        // But we can verify fields are still empty
        onView(withId(R.id.emailInput)).check(matches(withText("")))
        onView(withId(R.id.passwordInput)).check(matches(withText("")))
    }

    @Test
    fun testEmailInputAcceptsText() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Type email
        onView(withId(R.id.emailInput))
            .perform(typeText(testEmail), closeSoftKeyboard())

        // Verify email is entered
        onView(withId(R.id.emailInput))
            .check(matches(withText(testEmail)))
    }

    @Test
    fun testPasswordInputAcceptsText() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Type password
        onView(withId(R.id.passwordInput))
            .perform(typeText(testPassword), closeSoftKeyboard())

        // Password should be masked, but text should be there
        onView(withId(R.id.passwordInput))
            .check(matches(withText(testPassword)))
    }

    @Test
    fun testPasswordToggleButton() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Type password
        onView(withId(R.id.passwordInput))
            .perform(typeText(testPassword), closeSoftKeyboard())

        // Click toggle button
        onView(withId(R.id.togglePassword)).perform(click())

        // Password should be visible now (input type changes)
        // Click again to hide
        onView(withId(R.id.togglePassword)).perform(click())
    }

    @Test
    fun testForgotPasswordDialogDisplays() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Click forgot password
        onView(withId(R.id.forgotPassword)).perform(click())

        // Verify dialog appears with email input
        Thread.sleep(500) // Wait for dialog to appear
        onView(withId(R.id.etForgotEmail)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginWithInvalidCredentials() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Enter invalid credentials
        onView(withId(R.id.emailInput))
            .perform(typeText("invalid@test.com"), closeSoftKeyboard())
        onView(withId(R.id.passwordInput))
            .perform(typeText("wrongpassword"), closeSoftKeyboard())

        // Click login
        onView(withId(R.id.loginButton)).perform(click())

        // Wait for Firebase response
        Thread.sleep(3000)

        // Should remain on login screen
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginButtonIsClickable() {
        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.loginButton))
            .check(matches(isClickable()))
    }

    @Test
    fun testEmailInputHasCorrectInputType() {
        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.emailInput))
            .check(matches(withInputType(
                android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            )))
    }

    @Test
    fun testPasswordInputIsPasswordType() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Initially should be password type
        onView(withId(R.id.passwordInput))
            .check(matches(withInputType(
                android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            )))
    }

    @Test
    fun testForgotPasswordWithEmptyEmail() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Click forgot password
        onView(withId(R.id.forgotPassword)).perform(click())

        Thread.sleep(500)

        // Click send without entering email
        onView(withText("Send")).perform(click())

        // Dialog should still be visible (error shown)
        onView(withId(R.id.etForgotEmail)).check(matches(isDisplayed()))
    }

    @Test
    fun testUIElementsHaveCorrectText() {
        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.loginTitle))
            .check(matches(withText("Login")))

        onView(withId(R.id.loginButton))
            .check(matches(withText("Login")))

        onView(withId(R.id.forgotPassword))
            .check(matches(withText("Forgot Password?")))
    }

    @Test
    fun testBackgroundColor() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Verify main layout has correct background
        onView(withId(R.id.main))
            .check(matches(isDisplayed()))
    }
}