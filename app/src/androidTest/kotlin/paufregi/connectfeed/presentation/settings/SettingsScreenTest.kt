package paufregi.connectfeed.presentation.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
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
import paufregi.connectfeed.core.models.Credential

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `Default values`() {
        composeTestRule.setContent {
            SettingsContent(state = SettingsState(Credential("userTest", "passTest")))
        }
        composeTestRule.onNodeWithText("Username").assertTextContains("userTest")
        composeTestRule.onNodeWithText("Password").assertTextContains("••••••••")
        composeTestRule.onNodeWithText("Save").assertIsEnabled()
    }

    @Test
    fun `Default values visible password`() {
        composeTestRule.setContent {
            SettingsContent(state = SettingsState(Credential("userTest", "passTest"), showPassword = true))
        }
        composeTestRule.onNodeWithText("Username").assertTextContains("userTest")
        composeTestRule.onNodeWithText("Password").assertTextContains("passTest")
        composeTestRule.onNodeWithText("Save").assertIsEnabled()
    }

    @Test
    fun `Save button disabled`() {
        composeTestRule.setContent {
            SettingsContent(state = SettingsState(Credential("", "")))
        }
        composeTestRule.onNodeWithText("Username").assertTextContains("")
        composeTestRule.onNodeWithText("Password").assertTextContains("")
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }
}