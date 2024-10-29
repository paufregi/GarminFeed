package paufregi.garminfeed.data.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
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
import paufregi.garminfeed.connectDispatcher
import paufregi.garminfeed.connectPort
import paufregi.garminfeed.core.models.Activity as CoreActivity
import paufregi.garminfeed.core.models.ActivityType as CoreActivityType
import paufregi.garminfeed.core.models.Course
import paufregi.garminfeed.core.models.EventType
import paufregi.garminfeed.core.models.Profile
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.cred
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.GarminDatabase
import paufregi.garminfeed.data.database.entities.CredentialEntity
import paufregi.garminfeed.garminSSODispatcher
import paufregi.garminfeed.garminSSOPort
import paufregi.garminfeed.garthDispatcher
import paufregi.garminfeed.garthPort
import paufregi.garminfeed.sslSocketFactory
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
class GarminRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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
    fun `Store credentials`() = runTest {
        repo.saveCredential(cred)
        val res = repo.getCredential()

        res.test{
            assertThat(awaitItem()).isEqualTo(cred)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Upload file`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))

        val testFile = File.createTempFile("test", "test")
        testFile.deleteOnExit()
        val res = repo.uploadFile(testFile)

        assertThat(res.isSuccessful).isTrue()
    }

    @Test
    fun `Get latest activities`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))

        val expected = listOf(
            CoreActivity(id = 1, name = "Activity 1", type = CoreActivityType.Cycling),
            CoreActivity(id = 2, name = "Activity 2", type = CoreActivityType.Cycling)
        )

        val res = repo.getLatestActivities(5)

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(expected)
    }

    @Test
    fun `Update activity`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))

        val activity = CoreActivity(id = 1, name = "activity", type = CoreActivityType.Cycling)
        val profile = Profile(activityName = "newName", eventType = EventType.transportation, activityType = CoreActivityType.Cycling, course = Course.work, water = 1)

        val res = repo.updateActivity(activity, profile)

        assertThat(res.isSuccessful).isTrue()
    }
}