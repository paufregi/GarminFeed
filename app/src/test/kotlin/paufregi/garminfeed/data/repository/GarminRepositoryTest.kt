package paufregi.garminfeed.data.repository

import android.util.Log
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.clearStaticMockk
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.garminfeed.core.models.Activity as CoreActivity
import paufregi.garminfeed.core.models.ActivityType as CoreActivityType
import paufregi.garminfeed.core.models.Course
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.core.models.EventType as CoreEventType
import paufregi.garminfeed.core.models.Profile
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.data.api.GarminConnect
import paufregi.garminfeed.data.api.models.Activity
import paufregi.garminfeed.data.api.models.ActivityType
import paufregi.garminfeed.data.api.models.EventType
import paufregi.garminfeed.data.api.models.Metadata
import paufregi.garminfeed.data.api.models.Summary
import paufregi.garminfeed.data.api.models.UpdateActivity
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.entities.CredentialEntity
import paufregi.garminfeed.data.datastore.TokenManager
import retrofit2.Response
import java.io.File

class GarminRepositoryTest {

    private lateinit var repo: GarminRepository
    private val garminDao = mockk<GarminDao>()
    private val garminConnect = mockk<GarminConnect>()
    private val tokenManager = mockk<TokenManager>()

    @Before
    fun setup(){
        repo = GarminRepository(garminDao, garminConnect, tokenManager)
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown(){
        clearAllMocks()
        clearStaticMockk(Log::class)
    }


    @Test
    fun `Save credentials`() = runTest {
        val cred = Credential(username = "user", password = "pass")

        coEvery { garminDao.saveCredential(any()) } returns Unit

        repo.saveCredential(cred)

        coVerify { garminDao.saveCredential(CredentialEntity(credential = cred)) }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Get credential`() = runTest {
        val cred = Credential(username = "user", password = "pass")
        coEvery { garminDao.getCredential() } returns flowOf(CredentialEntity(credential = cred))

        val res = repo.getCredential()

        res.test {
            assertThat(awaitItem()).isEqualTo(cred)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { garminDao.getCredential() }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Upload file`() = runTest {
        val testFile = File.createTempFile("test", "test")

        coEvery { garminConnect.uploadFile(any()) } returns Response.success(Unit)

        val res = repo.uploadFile(testFile)

        assertThat(res.isSuccessful).isTrue()
        coVerify { garminConnect.uploadFile(any()) }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Upload file - failure`() = runTest {
        val testFile = File.createTempFile("test", "test")

        coEvery { garminConnect.uploadFile(any()) } returns Response.error<Unit?>(400, "error".toResponseBody("text/plain; charset=UTF-8".toMediaType()))

        val res = repo.uploadFile(testFile)

        assertThat(res.isSuccessful).isFalse()
        coVerify { garminConnect.uploadFile(any()) }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Get latest activities`() = runTest {
        val activities = listOf(
            Activity(id = 1, name = "activity_1", type = ActivityType(id = 1, key = "running")),
            Activity(id = 2, name = "activity_2", type = ActivityType(id = 10, key = "road_biking"))
        )
        coEvery { garminConnect.getLatestActivity(any()) } returns Response.success(activities)

        val expected = activities.map { it.toCore() }

        val res = repo.getLatestActivities(limit = 5)

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(expected)
        coVerify { garminConnect.getLatestActivity(5) }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Get latest activities - empty list`() = runTest {
        coEvery { garminConnect.getLatestActivity(any()) } returns Response.success(emptyList<Activity>())

        val res = repo.getLatestActivities(limit = 5)

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(emptyList<CoreActivity>())
        coVerify { garminConnect.getLatestActivity(5) }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Get latest activities - null`() = runTest {
        coEvery { garminConnect.getLatestActivity(any()) } returns Response.success(null)

        val res = repo.getLatestActivities(limit = 5)

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(emptyList<CoreActivity>())
        coVerify { garminConnect.getLatestActivity(5) }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Get latest activities - failure`() = runTest {
        coEvery { garminConnect.getLatestActivity(any()) } returns Response.error<List<Activity>>(400, "error".toResponseBody("text/plain; charset=UTF-8".toMediaType()))

        val res = repo.getLatestActivities(limit = 5)

        assertThat(res.isSuccessful).isFalse()
        coVerify { garminConnect.getLatestActivity(5) }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Update activity`() = runTest {
        coEvery { garminConnect.updateActivity(any(), any()) } returns Response.success(Unit)
        val activity = CoreActivity(id = 1, name = "activity", type = CoreActivityType.Cycling)
        val profile = Profile(activityName = "newName", eventType = CoreEventType.transportation, activityType = CoreActivityType.Cycling, course = Course(1, "course"), water = 2)

        val expectedRequest = UpdateActivity(
            id = 1,
            name = "newName",
            eventType = EventType(5, "transportation"),
            metadata = Metadata(1),
            summary = Summary(2, null, null)
        )

        val res = repo.updateActivity(activity, profile)

        assertThat(res.isSuccessful).isTrue()
        coVerify { garminConnect.updateActivity(1, expectedRequest) }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Update activity - failure`() = runTest {
        coEvery { garminConnect.updateActivity(any(), any()) } returns Response.error<Unit>(400, "error".toResponseBody("text/plain; charset=UTF-8".toMediaType()))
        val activity = CoreActivity(id = 1, name = "activity", type = CoreActivityType.Cycling)
        val profile = Profile(activityName = "newName", eventType = CoreEventType.transportation, activityType = CoreActivityType.Cycling, course = Course(1, "course"), water = 2)

        val expectedRequest = UpdateActivity(
            id = 1,
            name = "newName",
            eventType = EventType(5, "transportation"),
            metadata = Metadata(1),
            summary = Summary(2, null, null)
        )

        val res = repo.updateActivity(activity, profile)

        assertThat(res.isSuccessful).isFalse()
        coVerify { garminConnect.updateActivity(1, expectedRequest) }
        confirmVerified(garminConnect)
    }
}