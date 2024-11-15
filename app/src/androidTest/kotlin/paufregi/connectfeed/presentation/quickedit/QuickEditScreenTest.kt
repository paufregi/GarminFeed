package paufregi.connectfeed.presentation.quickedit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class QuickEditScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val activities = listOf(
        Activity(1L, "Running", ActivityType.Running),
        Activity(2L, "Cycling", ActivityType.Cycling)
    )

    val profiles = listOf(
        Profile("profile1", EventType.transportation, ActivityType.Running, Course.commuteHome, 1),
        Profile("profile2", EventType.transportation, ActivityType.Cycling, Course.commuteWork, 1),
        Profile("profile3", EventType.transportation, ActivityType.Running, Course.commuteHome, 1)
    )

    @Test
    fun `Default values`() {
        composeTestRule.setContent {
            QuickEditScreen(state = QuickEditState(
                activities = activities,
                allProfiles = profiles,
                availableProfiles = profiles
            ))
        }
        composeTestRule.onNodeWithText("Activity").isDisplayed()
        composeTestRule.onNodeWithText("Profile").isDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `Loading spinner`() {
        composeTestRule.setContent {
            QuickEditScreen(state = QuickEditState(
                loading = true,
                activities = activities,
                allProfiles = profiles,
                availableProfiles = profiles
            ))
        }
        composeTestRule.onNodeWithTag("loading").isDisplayed()
    }

    @Test
    fun `Values selected - running`() {
        composeTestRule.setContent {
            QuickEditScreen(state = QuickEditState(
                activities = activities,
                allProfiles = profiles,
                availableProfiles = profiles,
                selectedActivity = activities[0],
                selectedProfile = profiles[0],
            ))
        }
        composeTestRule.onNodeWithText("Activity").assertTextContains(activities[0].name)
        composeTestRule.onNodeWithText("Profile").assertTextContains(profiles[0].activityName)
    }

    @Test
    fun `Values selected - cycling`() {
        composeTestRule.setContent {
            QuickEditScreen(state = QuickEditState(
                activities = activities,
                allProfiles = profiles,
                availableProfiles = profiles,
                selectedActivity = activities[1],
                selectedProfile = profiles[1],
                selectedEffort = 50f,
                selectedFeel = 50f,
            ))
        }
        composeTestRule.onNodeWithText("Activity").assertTextContains(activities[1].name)
        composeTestRule.onNodeWithText("Profile").assertTextContains(profiles[1].activityName)
        composeTestRule.onNodeWithText("5 - Hard").isDisplayed()
        composeTestRule.onNodeWithText("Normal").isDisplayed()
    }
}