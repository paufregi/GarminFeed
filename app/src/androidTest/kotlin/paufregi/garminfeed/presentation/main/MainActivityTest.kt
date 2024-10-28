package paufregi.garminfeed.presentation.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.data.repository.GarminRepository

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var repo: GarminRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `Home page`() {
        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithText("Setup credential").assertIsDisplayed()
    }

    @Test
    fun `Setup credential`() = runTest{
        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithText("Setup").performClick()

        composeTestRule.onNodeWithText("Username").performTextInput("user")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithText("All good").assertIsDisplayed()
        val res = repo.getCredential()
        res.test{
            assertThat(awaitItem()).isEqualTo(Credential("user", "pass"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Clear cache`() = runTest{
        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithText("Clear cache").performClick()
        composeTestRule.onNodeWithText("Cache cleared").assertIsDisplayed()
    }

    @Test
    fun `Update activity`() = runTest{
        //TODO; write test
    }
}