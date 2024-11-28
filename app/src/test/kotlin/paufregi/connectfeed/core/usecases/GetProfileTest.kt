package paufregi.connectfeed.core.usecases

import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.data.repository.GarminRepository

class GetProfileTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: GetProfile

    @Before
    fun setup(){
        useCase = GetProfile(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Get profile`() = runTest {
        val profile = Profile(
            id = 1,
            name = "Commute to home",
            rename = true,
            eventType = EventType(id = 1, name = "event 1"),
            activityType = ActivityType.Cycling,
            course = Course(id = 1, name = "course 1", type = ActivityType.Cycling),
            water = 550
        )

        coEvery { repo.getProfile(any()) } returns profile

        val res = useCase(1)

        assertThat(res).isEqualTo(profile)
        coVerify { repo.getProfile(1) }
        confirmVerified(repo)
    }

    @Test
    fun `No profile`() = runTest {
        coEvery { repo.getProfile(any()) } returns null

        val res = useCase(1)

        assertThat(res).isNull()
        coVerify { repo.getProfile(1) }
        confirmVerified(repo)
    }
}