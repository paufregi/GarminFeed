package paufregi.garminfeed.presentation.quickedit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.ActivityType
import paufregi.garminfeed.core.models.Course
import paufregi.garminfeed.core.models.EventType
import paufregi.garminfeed.core.models.Profile

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class QuickEditScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val activities = listOf(
        Activity(1L, "Running", ActivityType.Running),
        Activity(2L, "Running", ActivityType.Running)
    )

    val profiles = listOf(
        Profile("profile1", EventType.transportation, ActivityType.Running, Course.home, 1),
        Profile("profile2", EventType.transportation, ActivityType.Cycling, Course.work, 1),
        Profile("profile3", EventType.transportation, ActivityType.Running, Course.home, 1)
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
    fun `Values selected`() {
        composeTestRule.setContent {
            QuickEditScreen(state = QuickEditState(
                activities = activities,
                allProfiles = profiles,
                availableProfiles = profiles,
                selectedActivity = activities[0],
                selectedProfile = profiles[0]
            ))
        }
        composeTestRule.onNodeWithText("Activity").assertTextContains(activities[0].name)
        composeTestRule.onNodeWithText("Profile").assertTextContains(profiles[0].activityName)
        composeTestRule.onNodeWithText("Save").assertIsEnabled()
    }
}