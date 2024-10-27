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
import kotlin.math.exp

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
    fun `Get profiles use-case`() = runTest{
        val expected = listOf(
            Profile(
                Course.home.name,
                EventType.transportation,
                ActivityType.Cycling,
                Course.home,
                500),
            Profile(
                Course.work.name,
                EventType.transportation,
                ActivityType.Cycling,
                Course.work,
                500),
        )

        val res = useCase()

        assertThat(res).isEqualTo(expected)
    }
}