package paufregi.connectfeed.core.usecases

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.clearStaticMockk
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository

class SaveProfileTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: SaveProfile

    @Before
    fun setup(){
        useCase = SaveProfile(repo)
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
    }

    @After
    fun tearDown(){
        clearAllMocks()
        clearStaticMockk(Log::class)
    }

    @Test
    fun `Save profile use-case`() = runTest {
        val profile = Profile(
            id = 1,
            name = "Commute to home",
            rename = true,
            eventType = EventType(id = 1, name = "event 1"),
            activityType = ActivityType.Cycling,
            course = Course(id = 1, name = "course 1", type = ActivityType.Cycling),
            water = 550
        )
        coEvery { repo.saveProfile(any()) } returns Unit

        val res = useCase(profile)

        assertThat(res).isInstanceOf(Result.Success(Unit).javaClass)
        coVerify { repo.saveProfile(profile) }
        confirmVerified(repo)
    }

    @Test
    fun `Invalid - No name`() = runTest {
        val profile = Profile(
            id = 1,
            name = "",
            rename = true,
            eventType = EventType(id = 1, name = "event 1"),
            activityType = ActivityType.Cycling,
            course = Course(id = 1, name = "course 1", type = ActivityType.Cycling),
            water = 550
        )

        val res = useCase(profile)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Name cannot be empty").javaClass)
    }

    @Test
    fun `Invalid - Strength profile with course`() = runTest {
        val profile = Profile(
            id = 1,
            name = "",
            rename = true,
            eventType = EventType(id = 1, name = "event 1"),
            activityType = ActivityType.Strength,
            course = Course(id = 1, name = "course 1", type = ActivityType.Cycling),
            water = 550
        )

        val res = useCase(profile)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Course must match activity type").javaClass)
    }

    @Test
    fun `Invalid - Course not matching activity type`() = runTest {
        val profile = Profile(
            id = 1,
            name = "",
            rename = true,
            eventType = EventType(id = 1, name = "event 1"),
            activityType = ActivityType.Running,
            course = Course(id = 1, name = "course 1", type = ActivityType.Cycling),
            water = 550
        )

        val res = useCase(profile)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Course must match activity type").javaClass)
    }
}