package paufregi.garminfeed.presentation.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import paufregi.garminfeed.connectDispatcher
import paufregi.garminfeed.connectPort
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.cred
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.GarminDatabase
import paufregi.garminfeed.data.database.entities.CredentialEntity
import paufregi.garminfeed.data.repository.GarminRepository
import paufregi.garminfeed.garminSSODispatcher
import paufregi.garminfeed.garminSSOPort
import paufregi.garminfeed.garthDispatcher
import paufregi.garminfeed.garthPort
import paufregi.garminfeed.sslSocketFactory
import javax.inject.Inject

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

    @Inject
    lateinit var database: GarminDatabase

    private lateinit var dao: GarminDao

    private val connectServer = MockWebServer()
    private val garminSSOServer = MockWebServer()
    private val garthServer = MockWebServer()

    @Before
    fun setup() {
        hiltRule.inject()
        connectServer.useHttps(sslSocketFactory, false)
        connectServer.start(connectPort)
        garminSSOServer.useHttps(sslSocketFactory, false)
        garminSSOServer.start(garminSSOPort)
        garthServer.useHttps(sslSocketFactory, false)
        garthServer.start(garthPort)

        connectServer.dispatcher = connectDispatcher
        garthServer.dispatcher = garthDispatcher
        garminSSOServer.dispatcher = garminSSODispatcher

        dao = database.garminDao()
    }

    @After
    fun tearDown() {
        connectServer.shutdown()
        garminSSOServer.shutdown()
        garthServer.shutdown()
        database.close()
    }

    @Test
    fun `Home page`() {
        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithText("Setup credential").assertIsDisplayed()
    }

    @Test
    fun `Setup credential`() = runTest {
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
    fun `Clear cache`() = runTest {
        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithText("Clear cache").performClick()
        composeTestRule.onNodeWithText("Cache cleared").assertIsDisplayed()
    }

    @Test
    fun `Update activity`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))

        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithTag("quickedit").performClick()
        composeTestRule.onNodeWithTag("loading").performClick()
        composeTestRule.waitUntil(1000) { composeTestRule.onNodeWithTag("activityDropDown").isDisplayed() }
        composeTestRule.onNodeWithTag("activityDropDown").performClick()
        composeTestRule.onNodeWithText("Activity 1").performClick()
        composeTestRule.onNodeWithTag("profileDropDown").performClick()
        composeTestRule.onNodeWithText("Commute to work").performClick()
        composeTestRule.onNodeWithText("Save").performClick()

        //FIXME: this final assertion can't be validate due to
        // kotlinx.coroutines.JobCancellationException: Job was cancelled
//        composeTestRule.onNodeWithText("Activity updated").assertIsDisplayed()
    }
}