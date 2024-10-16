package paufregi.garminfeed.presentation.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.core.usecases.GetCredentialUseCase
import paufregi.garminfeed.data.repository.GarminRepository

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `Setup done`() {
        composeTestRule.setContent {
            HomeScreen(state = HomeState(setupDone = true))
        }
        composeTestRule.onNodeWithText("All good").assertIsDisplayed()
    }

    @Test
    fun `Setup not done`() {
        composeTestRule.setContent {
            HomeScreen(state = HomeState(setupDone = false))
        }
        composeTestRule.onNodeWithText("Setup credential").assertIsDisplayed()
    }
}