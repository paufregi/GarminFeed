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
import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository

class GetLatestActivitiesTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: GetLatestActivities

    @Before
    fun setup(){
        useCase = GetLatestActivities(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Get latest activities`() = runTest {
        val activities = listOf(
            Activity(id = 1, name = "name", type = ActivityType.Running)
        )
        coEvery { repo.getLatestActivities(any()) } returns Result.Success(activities)
        val res = useCase()

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(activities)
        coVerify { repo.getLatestActivities(5) }
        confirmVerified(repo)
    }

    @Test
    fun `Get latest activities - failed`() = runTest {
        coEvery { repo.getLatestActivities(any()) } returns Result.Failure("Failed")
        val res = useCase()

        assertThat(res.isSuccessful).isFalse()
        coVerify { repo.getLatestActivities(5) }
        confirmVerified(repo)
    }
}