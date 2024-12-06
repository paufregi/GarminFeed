package paufregi.connectfeed.core.usecases

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.data.repository.GarminRepository

class GetProfilesTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: GetProfiles

    @Before
    fun setup(){
        useCase = GetProfiles(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Get profiles`() = runTest {
        val profiles = listOf(
            Profile(
                id = 1,
                name = "profile 1",
                rename = true,
                eventType = EventType(id = 1, name = "event 1"),
                activityType = ActivityType.Cycling,
                course = Course(id = 1, name = "course 1", type = ActivityType.Cycling),
                water = 550),
            Profile(
                id = 2,
                name = "profile 2",
                rename = true,
                eventType = EventType(id = 1, name = "event 2"),
                activityType = ActivityType.Running,
                course = Course(id = 2, name = "course 2", type = ActivityType.Running)),
        )

        coEvery { repo.getAllProfiles() } returns flowOf(profiles)

        val res = useCase()
        res.test {
            assertThat(awaitItem()).isEqualTo(profiles)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { repo.getAllProfiles() }
        confirmVerified(repo)
    }

    @Test
    fun `Get profiles - empty list`() = runTest {
        coEvery { repo.getAllProfiles() } returns flowOf(emptyList<Profile>())

        val res = useCase()
        res.test {
            assertThat(awaitItem()).isEqualTo(emptyList<Profile>())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { repo.getAllProfiles() }
        confirmVerified(repo)
    }
}