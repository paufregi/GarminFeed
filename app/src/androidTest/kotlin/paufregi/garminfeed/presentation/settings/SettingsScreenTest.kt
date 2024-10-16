package paufregi.garminfeed.presentation.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertValueEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import paufregi.garminfeed.core.models.Credential

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `Default values`() {
        composeTestRule.setContent {
            SettingsScreen(state = SettingsState(Credential("userTest", "passTest")))
        }
        composeTestRule.onNodeWithText("Username").assertTextContains("userTest")
        composeTestRule.onNodeWithText("Password").assertTextContains("••••••••")
    }

    @Test
    fun `Default values visible password`() {
        composeTestRule.setContent {
            SettingsScreen(state = SettingsState(Credential("userTest", "passTest"), showPassword = true))
        }
        composeTestRule.onNodeWithText("Username").assertTextContains("userTest")
        composeTestRule.onNodeWithText("Password").assertTextContains("passTest")
    }
}