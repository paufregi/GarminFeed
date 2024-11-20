package paufregi.connectfeed.presentation.syncWeight

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.presentation.syncweight.SyncWeightScreen
import paufregi.connectfeed.presentation.syncweight.SyncWeightState

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class SyncWeightScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `Status idle`() {
        composeTestRule.setContent {
            SyncWeightScreen(state = SyncWeightState.Idle)
        }
        composeTestRule.onNodeWithText("Don't know what to do").assertIsDisplayed()
    }

    @Test
    fun `Status uploading`() {
        composeTestRule.setContent {
            SyncWeightScreen(state = SyncWeightState.Uploading)
        }
        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun `Status success`() {
        composeTestRule.setContent {
            SyncWeightScreen(state = SyncWeightState.Success)
        }
        composeTestRule.onNodeWithText("Sync succeeded").assertIsDisplayed()
    }

    @Test
    fun `Status failure`() {
        composeTestRule.setContent {
            SyncWeightScreen(state = SyncWeightState.Failure)
        }
        composeTestRule.onNodeWithText("Sync failed").assertIsDisplayed()
    }
}