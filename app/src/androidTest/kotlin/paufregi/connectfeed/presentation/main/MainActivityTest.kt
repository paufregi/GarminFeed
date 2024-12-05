package paufregi.connectfeed.presentation.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingRegistry
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
import paufregi.connectfeed.connectDispatcher
import paufregi.connectfeed.connectPort
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.cred
import paufregi.connectfeed.data.database.GarminDao
import paufregi.connectfeed.data.database.GarminDatabase
import paufregi.connectfeed.data.database.entities.CredentialEntity
import paufregi.connectfeed.data.database.entities.ProfileEntity
import paufregi.connectfeed.data.repository.GarminRepository
import paufregi.connectfeed.garminSSODispatcher
import paufregi.connectfeed.garminSSOPort
import paufregi.connectfeed.garthDispatcher
import paufregi.connectfeed.garthPort
import paufregi.connectfeed.presentation.ui.utils.GlobalIdlingResource
import paufregi.connectfeed.sslSocketFactory
import javax.inject.Inject

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()
    private lateinit var loadingResource: IdlingResource

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

        IdlingRegistry.getInstance().register(GlobalIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        connectServer.shutdown()
        garminSSOServer.shutdown()
        garthServer.shutdown()
        database.close()

        IdlingRegistry.getInstance().unregister(GlobalIdlingResource.countingIdlingResource)

    }

    @Test
    fun `Home page - no credential`() {
        val a = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithText("Please setup your credential").assertIsDisplayed()
    }

    @Test
    fun `Setup credential`() = runTest {
        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithTag("nav_settings").performClick()

        composeTestRule.onNodeWithText("Username").performTextInput("user")
        composeTestRule.onNodeWithText("Password").performTextInput("pass")
        composeTestRule.onNodeWithText("Save").performClick()

        val res = repo.getCredential()
        res.test{
            assertThat(awaitItem()).isEqualTo(Credential("user", "pass"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Create profile`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))

        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithTag("nav_profiles").performClick()
        composeTestRule.onNodeWithTag("create_profile").performClick()

        composeTestRule.waitUntil { composeTestRule.onNodeWithText("Name").isDisplayed() }

        composeTestRule.onNodeWithText("Name").performTextInput("Profile 1")
        composeTestRule.onNodeWithText("Activity Type").performClick()
        composeTestRule.onNodeWithText("Running").performClick()
        composeTestRule.onNodeWithText("Event Type").performClick()
        composeTestRule.onNodeWithText("Race").performClick()
        composeTestRule.onNodeWithText("Course").performClick()
        composeTestRule.onNodeWithText("Course 1").performClick()
        composeTestRule.onNodeWithText("Water").performTextInput("500")
        composeTestRule.onNodeWithText("Save").performClick()



        val res = repo.getAllProfiles()
        res.test{
            val profiles = awaitItem()
            assertThat(profiles.size).isEqualTo(1)
            assertThat(profiles.first().name).isEqualTo("Profile 1")
            assertThat(profiles.first().activityType).isEqualTo(ActivityType.Running)
            assertThat(profiles.first().eventType).isEqualTo(EventType(id = 1, name = "Race"))
            assertThat(profiles.first().course).isEqualTo(Course(id = 1, name = "Course 1", type = ActivityType.Running))
            assertThat(profiles.first().water).isEqualTo(500)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Update profile`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))
        dao.saveProfile(ProfileEntity(id = 5, name = "Profile 1", activityType = ActivityType.Running, eventType = EventType(id = 1, name = "Race")))

        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithTag("nav_profiles").performClick()
        composeTestRule.onNodeWithTag("loading").isDisplayed()

        composeTestRule.waitUntil { composeTestRule.onNodeWithText("Profile 1").isDisplayed() }
        composeTestRule.onNodeWithText("Profile 1").performClick()

        composeTestRule.waitUntil { composeTestRule.onNodeWithText("Name").isDisplayed() }
        composeTestRule.onNodeWithText("Name").performTextClearance()
        composeTestRule.onNodeWithText("Name").performTextInput("Profile 2")
        composeTestRule.onNodeWithText("Activity Type").performClick()
        composeTestRule.onNodeWithText("Cycling").performClick()
        composeTestRule.onNodeWithText("Event Type").performClick()
        composeTestRule.onNodeWithText("Training").performClick()
        composeTestRule.onNodeWithText("Course").performClick()
        composeTestRule.onNodeWithText("Course 2").performClick()
        composeTestRule.onNodeWithText("Water").performTextInput("100")
        composeTestRule.onNodeWithText("Save").performClick()

        val profile = repo.getProfile(5)
        assertThat(profile?.name).isEqualTo("Profile 2")
        assertThat(profile?.activityType).isEqualTo(ActivityType.Cycling)
        assertThat(profile?.eventType).isEqualTo(EventType(id = 2, name = "Training"))
        assertThat(profile?.course).isEqualTo(Course(id = 2, name = "Course 2", type = ActivityType.Cycling))
        assertThat(profile?.water).isEqualTo(100)
    }

    @Test
    fun `Delete profile`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))
        dao.saveProfile(ProfileEntity(id = 10, name = "Profile 1", activityType = ActivityType.Running, eventType = EventType(id = 1, name = "Race")))

        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithTag("nav_profiles").performClick()
        composeTestRule.onNodeWithTag("loading").isDisplayed()

        composeTestRule.waitUntil { composeTestRule.onNodeWithText("Profile 1").isDisplayed() }
        composeTestRule.onNodeWithTag("delete_profile_10").performClick()

        composeTestRule.onNodeWithText("Profile 1").isNotDisplayed()
        val profile = repo.getProfile(10)
        assertThat(profile).isNull()
    }

    @Test
    fun `Update activity`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))
        dao.saveProfile(ProfileEntity(name = "Profile 1", activityType = ActivityType.Cycling, eventType = EventType(id = 1, name = "Race")))

        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithTag("loading").isDisplayed()
        composeTestRule.waitUntil { composeTestRule.onNodeWithText("Activity").isDisplayed() }
        composeTestRule.onNodeWithText("Activity").performClick()
        composeTestRule.onNodeWithText("Activity 1").performClick()
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.onNodeWithText("Profile 1").performClick()
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithText("Save").isDisplayed()
        composeTestRule.waitUntil { composeTestRule.onNodeWithText("Activity updated").isDisplayed() }
        composeTestRule.onNodeWithText("Activity updated").assertIsDisplayed()
    }
}