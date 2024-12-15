package paufregi.connectfeed.data.repository

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
import paufregi.connectfeed.core.models.Activity as CoreActivity
import paufregi.connectfeed.core.models.ActivityType as CoreActivityType
import paufregi.connectfeed.core.models.Course as CoreCourse
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.EventType as CoreEventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.api.GarminConnect
import paufregi.connectfeed.data.api.models.Activity
import paufregi.connectfeed.data.api.models.ActivityType
import paufregi.connectfeed.data.api.models.Course
import paufregi.connectfeed.data.api.models.EventType
import paufregi.connectfeed.data.api.models.Metadata
import paufregi.connectfeed.data.api.models.Summary
import paufregi.connectfeed.data.api.models.UpdateActivity
import paufregi.connectfeed.data.database.GarminDao
import paufregi.connectfeed.data.database.entities.CredentialEntity
import paufregi.connectfeed.data.database.entities.ProfileEntity
import paufregi.connectfeed.data.datastore.UserDataStore
import retrofit2.Response
import java.io.File

class GarminRepositoryTest {

    private lateinit var repo: GarminRepository
    private val garminDao = mockk<GarminDao>()
    private val garminConnect = mockk<GarminConnect>()
    private val userDataStore = mockk<UserDataStore>()

    @Before
    fun setup(){
        repo = GarminRepository(garminDao, garminConnect, userDataStore)
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
    fun `Save credential`() = runTest {
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
    fun `Save profile`() = runTest {
        val profile = Profile(
            name = "profile",
            eventType = CoreEventType(id = 1, name = "event"),
            activityType = CoreActivityType.Cycling,
            course = CoreCourse(1, "course", CoreActivityType.Cycling),
            water = 2
        )

        val profileEntity  = ProfileEntity(
            name = "profile",
            eventType = CoreEventType(id = 1, name = "event"),
            activityType = CoreActivityType.Cycling,
            course = CoreCourse(1, "course", CoreActivityType.Cycling),
            water = 2
        )

        coEvery { garminDao.saveProfile(any()) } returns Unit

        repo.saveProfile(profile)

        coVerify { garminDao.saveProfile(profileEntity) }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Delete profile`() = runTest {
        val profile = Profile(
            id = 1,
            name = "profile",
            eventType = CoreEventType(id = 1, name = "event"),
            activityType = CoreActivityType.Cycling,
            course = CoreCourse(1, "course", CoreActivityType.Cycling),
        )

        val profileEntity  = ProfileEntity(
            id = 1,
            name = "profile",
            eventType = CoreEventType(id = 1, name = "event"),
            activityType = CoreActivityType.Cycling,
            course = CoreCourse(1, "course", CoreActivityType.Cycling),
        )

        coEvery { garminDao.deleteProfile(any()) } returns Unit

        repo.deleteProfile(profile)

        coVerify { garminDao.deleteProfile(profileEntity) }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Get profile`() = runTest {
        val profile = Profile(
            id = 1,
            name = "profile",
            eventType = CoreEventType(id = 1, name = "event"),
            activityType = CoreActivityType.Cycling,
            course = CoreCourse(1, "course", CoreActivityType.Cycling),
            water = 2
        )
        val profileEntity  = ProfileEntity(
            id = 1,
            name = "profile",
            eventType = CoreEventType(id = 1, name = "event"),
            activityType = CoreActivityType.Cycling,
            course = CoreCourse(1, "course", CoreActivityType.Cycling),
            water = 2
        )

        coEvery { garminDao.getProfile(any()) } returns profileEntity

        val res = repo.getProfile(1)

        assertThat(res).isEqualTo(profile)

        coVerify { garminDao.getProfile(1) }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Get profile - no result`() = runTest {
                coEvery { garminDao.getProfile(any()) } returns null

        val res = repo.getProfile(1)

        assertThat(res).isNull()

        coVerify { garminDao.getProfile(1) }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Get profiles`() = runTest {
        val profiles = listOf(
            Profile(
                id = 1,
                name = "profile 1",
                eventType = CoreEventType(id = 1, name = "event 1"),
                activityType = CoreActivityType.Cycling,
                course = CoreCourse(1, "course 1", CoreActivityType.Cycling),
                water = 2
            ),
            Profile(
                id = 2,
                name = "profile 2",
                eventType = CoreEventType(id = 2, name = "event 2"),
                activityType = CoreActivityType.Running,
                course = CoreCourse(2, "course 2", CoreActivityType.Running),
            )
        )
        val profileEntities = listOf(
            ProfileEntity(
                id = 1,
                name = "profile 1",
                eventType = CoreEventType(id = 1, name = "event 1"),
                activityType = CoreActivityType.Cycling,
                course = CoreCourse(1, "course 1", CoreActivityType.Cycling),
                water = 2
            ),
            ProfileEntity(
                id = 2,
                name = "profile 2",
                eventType = CoreEventType(id = 2, name = "event 2"),
                activityType = CoreActivityType.Running,
                course = CoreCourse(2, "course 2", CoreActivityType.Running),
            )
        )

        coEvery { garminDao.getAllProfiles() } returns flowOf(profileEntities)

        val res = repo.getAllProfiles()

        res.test {
            assertThat(awaitItem()).isEqualTo(profiles)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { garminDao.getAllProfiles() }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Get profiles - empty list`() = runTest {
        coEvery { garminDao.getAllProfiles() } returns flowOf(emptyList<ProfileEntity>())

        val res = repo.getAllProfiles()

        res.test {
            assertThat(awaitItem()).isEqualTo(emptyList<Profile>())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { garminDao.getAllProfiles() }
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
    fun `Get courses`() = runTest {
        val courses = listOf(
            Course(id = 1, name = "course 1", type = ActivityType(id = 1, key = "running")),
            Course(id = 2, name = "course 2", type = ActivityType(id = 10, key = "road_biking"))
        )
        coEvery { garminConnect.getCourses() } returns Response.success(courses)

        val expected = listOf(
            CoreCourse(id = 1, name = "course 1", type = CoreActivityType.Running),
            CoreCourse(id = 2, name = "course 2", type = CoreActivityType.Cycling),
        )

        val res = repo.getCourses()

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(expected)
        coVerify { garminConnect.getCourses() }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Get courses - empty list`() = runTest {
        coEvery { garminConnect.getCourses() } returns Response.success(emptyList())

        val res = repo.getCourses()

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(emptyList<CoreCourse>())
        coVerify { garminConnect.getCourses() }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Get courses - null`() = runTest {
        coEvery { garminConnect.getCourses() } returns Response.success(null)

        val res = repo.getCourses()

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(emptyList<CoreCourse>())
        coVerify { garminConnect.getCourses() }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Get courses - failure`() = runTest {
        coEvery { garminConnect.getCourses() } returns Response.error<List<Course>>(400, "error".toResponseBody("text/plain; charset=UTF-8".toMediaType()))

        val res = repo.getCourses()

        assertThat(res.isSuccessful).isFalse()
        coVerify { garminConnect.getCourses() }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Get event types`() = runTest {
        val eventTypes = listOf(
            EventType(id = 1, key = "race"),
            EventType(id = 2, key = "training")
        )
        coEvery { garminConnect.getEventTypes() } returns Response.success(eventTypes)

        val expected = listOf(
            CoreEventType(id = 1, name = "Race"),
            CoreEventType(id = 2, name = "Training"),
        )

        val res = repo.getEventTypes()

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(expected)
        coVerify { garminConnect.getEventTypes() }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Get event types - empty list`() = runTest {
        coEvery { garminConnect.getEventTypes() } returns Response.success(emptyList())

        val res = repo.getEventTypes()

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(emptyList<CoreEventType>())
        coVerify { garminConnect.getEventTypes() }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Get event types - null`() = runTest {
        coEvery { garminConnect.getEventTypes() } returns Response.success(null)

        val res = repo.getEventTypes()

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(emptyList<CoreEventType>())
        coVerify { garminConnect.getEventTypes() }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Get event types - failure`() = runTest {
        coEvery { garminConnect.getEventTypes() } returns Response.error<List<EventType>>(400, "error".toResponseBody("text/plain; charset=UTF-8".toMediaType()))

        val res = repo.getEventTypes()

        assertThat(res.isSuccessful).isFalse()
        coVerify { garminConnect.getEventTypes() }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Update activity`() = runTest {
        coEvery { garminConnect.updateActivity(any(), any()) } returns Response.success(Unit)
        val activity = CoreActivity(id = 1, name = "activity", type = CoreActivityType.Cycling)
        val profile = Profile(
            name = "newName",
            eventType = CoreEventType(id = 1, name = "event"),
            activityType = CoreActivityType.Cycling,
            course = CoreCourse(1, "course", CoreActivityType.Cycling),
            water = 2
        )

        val expectedRequest = UpdateActivity(
            id = 1,
            name = "newName",
            eventType = EventType(id = 1, key = "event"),
            metadata = Metadata(1),
            summary = Summary(2, 50f, 80f)
        )

        val res = repo.updateActivity(activity, profile, 50f, 80f)

        assertThat(res.isSuccessful).isTrue()
        coVerify { garminConnect.updateActivity(1, expectedRequest) }
        confirmVerified(garminConnect)
    }

    @Test
    fun `Update activity - failure`() = runTest {
        coEvery { garminConnect.updateActivity(any(), any()) } returns Response.error<Unit>(400, "error".toResponseBody("text/plain; charset=UTF-8".toMediaType()))
        val activity = CoreActivity(id = 1, name = "activity", type = CoreActivityType.Cycling)
        val profile = Profile(
            name = "newName",
            eventType = CoreEventType(id = 1, name = "event"),
            activityType = CoreActivityType.Cycling,
            course = CoreCourse(1, "course", CoreActivityType.Cycling),
            water = 2
        )

        val expectedRequest = UpdateActivity(
            id = 1,
            name = "newName",
            eventType = EventType(id = 1, key = "event"),
            metadata = Metadata(courseId = 1),
            summary = Summary(water = 2, feel = null, effort = null)
        )

        val res = repo.updateActivity(activity, profile, null, null)

        assertThat(res.isSuccessful).isFalse()
        coVerify { garminConnect.updateActivity(1, expectedRequest) }
        confirmVerified(garminConnect)
    }
}