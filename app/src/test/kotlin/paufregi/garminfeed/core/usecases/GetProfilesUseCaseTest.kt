package paufregi.garminfeed.core.usecases

import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.garminfeed.core.models.ActivityType
import paufregi.garminfeed.core.models.Course
import paufregi.garminfeed.core.models.EventType
import paufregi.garminfeed.core.models.Profile
import paufregi.garminfeed.data.repository.GarminRepository

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
                Course.commuteHome.name,
                EventType.transportation,
                ActivityType.Cycling,
                Course.commuteHome,
                500),
            Profile(
                Course.commuteWork.name,
                EventType.transportation,
                ActivityType.Cycling,
                Course.commuteWork,
                500),
        )

        val res = useCase()

        assertThat(res).isEqualTo(expected)
    }
}