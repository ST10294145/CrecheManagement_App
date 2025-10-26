package com.crecheconnect.crechemanagement_app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RegisterActivityInstrumentalTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @Before
    fun setup() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        auth.signOut()
    }

    @After
    fun tearDown() {
        auth.signOut()
    }

    @Test
    fun testRegisterScreenDisplayed() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Verify UI elements are displayed (scroll to elements that might be off-screen)
        onView(withId(R.id.logoImage)).check(matches(isDisplayed()))
        onView(withId(R.id.roleSpinner)).check(matches(isDisplayed()))
        onView(withId(R.id.emailInput))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.passwordInput))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.signUpButton))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRoleSpinnerDisplaysCorrectly() {
        ActivityScenario.launch(RegisterActivity::class.java)

        onView(withId(R.id.roleSpinner))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
    }

    @Test
    fun testParentSectionVisibilityOnRoleSelection() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select parent role (index 0)
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(0).perform(click())

        // Wait for UI update
        Thread.sleep(500)

        // Parent section should be visible
        onView(withId(R.id.parentSection))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAdminSectionVisibilityOnRoleSelection() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select admin/staff role (index 1)
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        Thread.sleep(500)

        // Admin section should be visible
        onView(withId(R.id.adminSection))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testParentFieldsAreDisplayed() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select parent role
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(0).perform(click())

        Thread.sleep(500)

        // Check all parent fields are visible
        onView(withId(R.id.parentName)).check(matches(isDisplayed()))
        onView(withId(R.id.PhoneNumber)).check(matches(isDisplayed()))
        onView(withId(R.id.homeAddress)).check(matches(isDisplayed()))
        onView(withId(R.id.childFullName)).check(matches(isDisplayed()))
        onView(withId(R.id.childDob)).check(matches(isDisplayed()))
        onView(withId(R.id.childGender)).check(matches(isDisplayed()))
        onView(withId(R.id.allergySpinner)).check(matches(isDisplayed()))
    }

    @Test
    fun testAdminFieldsAreDisplayed() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select admin role
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        Thread.sleep(500)

        // Check admin fields are visible
        onView(withId(R.id.adminFullName)).check(matches(isDisplayed()))
        onView(withId(R.id.adminPhoneNumber)).check(matches(isDisplayed()))
    }

    @Test
    fun testAllergyDetailsVisibilityOnYesSelection() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select parent role
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(0).perform(click())

        Thread.sleep(500)

        // Initially, allergyDetails should be gone
        onView(withId(R.id.allergyDetails))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // Select "Yes" from allergy spinner
        onView(withId(R.id.allergySpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Yes")))
            .perform(click())

        Thread.sleep(300)

        // Now allergyDetails should be visible
        onView(withId(R.id.allergyDetails))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAllergyDetailsHiddenOnNoSelection() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select parent role
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(0).perform(click())

        Thread.sleep(500)

        // Select "No" from allergy spinner
        onView(withId(R.id.allergySpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("No")))
            .perform(click())

        Thread.sleep(300)

        // allergyDetails should be gone
        onView(withId(R.id.allergyDetails))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun testSignUpButtonVisibleAndClickable() {
        ActivityScenario.launch(RegisterActivity::class.java)

        onView(withId(R.id.signUpButton))
            .perform(scrollTo()) // makes it visible
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
    }


    @Test
    fun testEmailInputAcceptsText() {
        ActivityScenario.launch(RegisterActivity::class.java)

        val testEmail = "parent@test.com"

        onView(withId(R.id.emailInput))
            .perform(scrollTo(), typeText(testEmail), closeSoftKeyboard())

        onView(withId(R.id.emailInput))
            .check(matches(withText(testEmail)))
    }

    @Test
    fun testPasswordInputAcceptsText() {
        ActivityScenario.launch(RegisterActivity::class.java)

        val testPassword = "password123"

        onView(withId(R.id.passwordInput))
            .perform(scrollTo(), typeText(testPassword), closeSoftKeyboard())

        onView(withId(R.id.passwordInput))
            .check(matches(withText(testPassword)))
    }

    @Test
    fun testParentNameInputAcceptsText() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select parent role
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(0).perform(click())

        Thread.sleep(500)

        val parentName = "John Doe"

        onView(withId(R.id.parentName))
            .perform(scrollTo(), typeText(parentName), closeSoftKeyboard())

        onView(withId(R.id.parentName))
            .check(matches(withText(parentName)))
    }

    @Test
    fun testChildNameInputAcceptsText() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select parent role
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(0).perform(click())

        Thread.sleep(500)

        val childName = "Jane Doe"

        onView(withId(R.id.childFullName))
            .perform(scrollTo(), typeText(childName), closeSoftKeyboard())

        onView(withId(R.id.childFullName))
            .check(matches(withText(childName)))
    }

    @Test
    fun testPhoneNumberInputAcceptsNumbers() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select parent role
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(0).perform(click())

        Thread.sleep(500)

        val phoneNumber = "0123456789"

        onView(withId(R.id.PhoneNumber))
            .perform(scrollTo(), typeText(phoneNumber), closeSoftKeyboard())

        onView(withId(R.id.PhoneNumber))
            .check(matches(withText(phoneNumber)))
    }

    @Test
    fun testAdminNameInputAcceptsText() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select admin role
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        Thread.sleep(500)

        val adminName = "Admin User"

        onView(withId(R.id.adminFullName))
            .perform(scrollTo(), typeText(adminName), closeSoftKeyboard())

        onView(withId(R.id.adminFullName))
            .check(matches(withText(adminName)))
    }

    @Test
    fun testScrollViewWorksCorrectly() {
        ActivityScenario.launch(RegisterActivity::class.java)

        // Select parent role to show all fields
        onView(withId(R.id.roleSpinner)).perform(click())
        onData(anything()).atPosition(0).perform(click())

        Thread.sleep(500)

        // Scroll to bottom
        onView(withId(R.id.signUpButton)).perform(scrollTo())

        // Button should be visible after scrolling
        onView(withId(R.id.signUpButton))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSignUpButtonHasCorrectText() {
        ActivityScenario.launch(RegisterActivity::class.java)

        onView(withId(R.id.signUpButton))
            .perform(scrollTo())
            .check(matches(withText("Sign up")))
    }

    @Test
    fun testSignUpButtonIsClickable() {
        ActivityScenario.launch(RegisterActivity::class.java)

        onView(withId(R.id.signUpButton))
            .perform(scrollTo())
            .check(matches(isClickable()))
    }

    @Test
    fun testEmailInputHasCorrectInputType() {
        ActivityScenario.launch(RegisterActivity::class.java)

        onView(withId(R.id.emailInput))
            .perform(scrollTo())
            .check(matches(withInputType(
                android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            )))
    }

    @Test
    fun testPasswordInputHasCorrectInputType() {
        ActivityScenario.launch(RegisterActivity::class.java)

        onView(withId(R.id.passwordInput))
            .perform(scrollTo())
            .check(matches(withInputType(
                android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            )))
    }
}