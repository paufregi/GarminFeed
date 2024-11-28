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
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository

class GetEventTypesTest {

    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: GetCourses

    @Before
    fun setup(){
        useCase = GetCourses(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Get event types`() = runTest {
        val eventTypes = listOf(
            EventType(id = 1, name = "event 1"),
            EventType(id = 2, name = "event 2"),
        )
        coEvery { repo.getEventTypes() } returns Result.Success(eventTypes)
        val res = useCase()

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(eventTypes)
        coVerify { repo.getCourses() }
        confirmVerified(repo)
    }

    @Test
    fun `Get event types - failure`() = runTest {
        coEvery { repo.getCourses() } returns Result.Failure("Failed")
        val res = useCase()

        assertThat(res.isSuccessful).isFalse()
        coVerify { repo.getCourses() }
        confirmVerified(repo)
    }
}