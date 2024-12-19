package paufregi.connectfeed.presentation.profile

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.presentation.ui.components.ProcessState

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `Default values`() {
        composeTestRule.setContent {
            ProfileContent(state = ProfileState(
                processState = ProcessState.Idle,
            ))
        }
        composeTestRule.onNodeWithText("Name").isDisplayed()
        composeTestRule.onNodeWithText("Activity Type").isDisplayed()
        composeTestRule.onNodeWithText("Event Type").isDisplayed()
        composeTestRule.onNodeWithText("Water").isDisplayed()
        composeTestRule.onNodeWithText("Rename activity").isDisplayed()
        composeTestRule.onNodeWithTag("rename_checkbox").assertIsOn()
        composeTestRule.onNodeWithText("Customizable water").isDisplayed()
        composeTestRule.onNodeWithTag("custom_water_checkbox").assertIsOff()
        composeTestRule.onNodeWithText("Feel & Effort").isDisplayed()
        composeTestRule.onNodeWithTag("feel_and_effort_checkbox").assertIsOff()
        composeTestRule.onNodeWithText("Cancel").isDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `Loading spinner`() {
        composeTestRule.setContent {
            ProfileContent(state = ProfileState(
                processState = ProcessState.Processing
            ))
        }
        composeTestRule.onNodeWithTag("loading").isDisplayed()
    }

    @Test
    fun `Edit profile`() {
        composeTestRule.setContent {
            ProfileContent(state = ProfileState(
                processState = ProcessState.Idle,
                profile = Profile(
                    name = "Profile 1",
                    activityType = ActivityType.Running,
                    eventType = EventType(id = 1, name = "Event 1"),
                    course = Course(id = 1, name = "Course 1", type = ActivityType.Running),
                    water = 10,
                    rename = true,
                    customWater = true,
                    feelAndEffort = true
                ))
            )
        }
        composeTestRule.onNodeWithText("Name").assertTextContains("Profile 1")
        composeTestRule.onNodeWithText("Activity Type").assertTextContains("Running")
        composeTestRule.onNodeWithText("Event Type").assertTextContains("Event 1")
        composeTestRule.onNodeWithText("Course").assertTextContains("Course 1")
        composeTestRule.onNodeWithText("Water").assertTextContains("10")
        composeTestRule.onNodeWithTag("rename_checkbox").assertIsOn()
        composeTestRule.onNodeWithTag("custom_water_checkbox").assertIsOn()
        composeTestRule.onNodeWithTag("feel_and_effort_checkbox").assertIsOn()
        composeTestRule.onNodeWithText("Cancel").isDisplayed()
        composeTestRule.onNodeWithText("Save").isDisplayed()
    }

    @Test
    fun `Invalid profile - no name`() {
        composeTestRule.setContent {
            ProfileContent(state = ProfileState(
                processState = ProcessState.Idle,
                profile = Profile(
                    name = "",
                    activityType = ActivityType.Running,
                    eventType = EventType(id = 1, name = "Event 1"),
                    course = Course(id = 1, name = "Course 1", type = ActivityType.Running),
                )
            ))
        }
        composeTestRule.onNodeWithText("Name").assertTextContains("")
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `Invalid profile - no event type`() {
        composeTestRule.setContent {
            ProfileContent(state = ProfileState(
                processState = ProcessState.Idle,
                profile = Profile(
                    name = "Profile 1",
                    activityType = ActivityType.Running,
                )
            ))
        }
        composeTestRule.onNodeWithText("Event Type").assertTextContains("")
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `No course - type any`() {
        composeTestRule.setContent {
            ProfileContent(state = ProfileState(
                processState = ProcessState.Idle,
                profile = Profile(
                    name = "Profile 1",
                    activityType = ActivityType.Any,
                    eventType = EventType(id = 1, name = "Event 1"),
                )
            ))
        }
        composeTestRule.onNodeWithText("Course").isNotDisplayed()
        composeTestRule.onNodeWithText("Save").isDisplayed()
    }

    @Test
    fun `No course - type strength`() {
        composeTestRule.setContent {
            ProfileContent(state = ProfileState(
                processState = ProcessState.Idle,
                profile = Profile(
                    name = "Profile 1",
                    activityType = ActivityType.Strength,
                    eventType = EventType(id = 1, name = "Event 1")
                )
            ))
        }
        composeTestRule.onNodeWithText("Course").isNotDisplayed()
        composeTestRule.onNodeWithText("Save").isDisplayed()
    }
}