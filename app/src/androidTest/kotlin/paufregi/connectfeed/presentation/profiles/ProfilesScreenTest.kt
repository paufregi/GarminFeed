package paufregi.connectfeed.presentation.profiles

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Profile

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class ProfilesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `Default values`() {
        composeTestRule.setContent {
            ProfilesContent(state = ProfilesState())
        }
        composeTestRule.onNodeWithText("No profile").isDisplayed()
        composeTestRule.onNodeWithTag("addProfile").isDisplayed()
    }

    @Test
    fun `Profile list`() {
        composeTestRule.setContent {
            ProfilesContent(state = ProfilesState(
                profiles = listOf(
                    Profile(name = "Profile 1", activityType = ActivityType.Running),
                    Profile(name = "Profile 2", activityType = ActivityType.Cycling),
                    Profile(name = "Profile 3", activityType = ActivityType.Running)
                )
            ))
        }
        composeTestRule.onNodeWithText("Profile 1").isDisplayed()
        composeTestRule.onNodeWithText("Profile 2").isDisplayed()
        composeTestRule.onNodeWithText("Profile 3").isDisplayed()
        composeTestRule.onNodeWithTag("addProfile").isDisplayed()
    }

}