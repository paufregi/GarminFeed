package paufregi.connectfeed.core.usecases

import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
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

class GetProfilesUseCaseTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: GetProfilesUseCase

    @Before
    fun setup(){
        useCase = GetProfilesUseCase(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Get profiles use-case`() = runTest {
        val expected = listOf(
            Profile(
                "Commute to home",
                EventType.transportation,
                ActivityType.Cycling,
                Course.commuteHome,
                550),
            Profile(
                "Commute to work",
                EventType.transportation,
                ActivityType.Cycling,
                Course.commuteWork,
                550),
            Profile(
                "Ponsonby/Westhaven",
                EventType.training,
                ActivityType.Running,
                Course.ponsonbyWesthaven),
            Profile(
                "Auckland CBD",
                EventType.training,
                ActivityType.Running,
                Course.aucklandCBD),
        )

        val res = useCase()

        assertThat(res).isEqualTo(expected)
    }
}