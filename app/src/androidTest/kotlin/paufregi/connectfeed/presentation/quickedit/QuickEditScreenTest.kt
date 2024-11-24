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
        Profile(name = "profile1", activityType = ActivityType.Running),
        Profile(name = "profile2", activityType = ActivityType.Cycling),
        Profile(name = "profile3", activityType = ActivityType.Running)
    )

    @Test
    fun `Default values`() {
        composeTestRule.setContent {
            QuickEditContent(state = QuickEditState(
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
            QuickEditContent(state = QuickEditState(
                processing = ProcessState.Processing,
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
            QuickEditContent(state = QuickEditState(
                activities = activities,
                allProfiles = profiles,
                availableProfiles = profiles,
                activity = activities[0],
                profile = profiles[0],
            ))
        }
        composeTestRule.onNodeWithText("Activity").assertTextContains(activities[0].name)
        composeTestRule.onNodeWithText("Profile").assertTextContains(profiles[0].name)
    }

    @Test
    fun `Values selected - cycling`() {
        composeTestRule.setContent {
            QuickEditContent(state = QuickEditState(
                activities = activities,
                allProfiles = profiles,
                availableProfiles = profiles,
                activity = activities[1],
                profile = profiles[1],
                effort = 50f,
                feel = 50f,
            ))
        }
        composeTestRule.onNodeWithText("Activity").assertTextContains(activities[1].name)
        composeTestRule.onNodeWithText("Profile").assertTextContains(profiles[1].name)
        composeTestRule.onNodeWithText("5 - Hard").isDisplayed()
        composeTestRule.onNodeWithText("Normal").isDisplayed()
    }
}