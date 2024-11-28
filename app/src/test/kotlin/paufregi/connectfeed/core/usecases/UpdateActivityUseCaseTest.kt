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
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository

class UpdateActivityUseCaseTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: UpdateActivity

    val activity = Activity(id = 1, name = "name", type = ActivityType.Running)
    val profile = Profile(
        name = "newName",
        eventType = EventType.transportation,
        activityType = ActivityType.Running,
        course = Course.commuteHome,
        water = 500
    )

    @Before
    fun setup(){
        useCase = UpdateActivity(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Update activity use-case`() = runTest {
        coEvery { repo.updateActivity(any(), any(), any(), any()) } returns Result.Success(Unit)
        val res = useCase(activity, profile, 50f, 90f)

        assertThat(res).isInstanceOf(Result.Success(Unit).javaClass)
        coVerify { repo.updateActivity(activity, profile, 50f, 90f) }
        confirmVerified(repo)
    }

    @Test
    fun `Invalid - no activity`() = runTest {
        val res = useCase(null, profile, null, null)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Validation error").javaClass)
    }

    @Test
    fun `Invalid - no profile`() = runTest {
        val res = useCase(activity, null, null, null)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Validation error").javaClass)
    }

    @Test
    fun `Invalid - both null`() = runTest {
        val res = useCase(null, null, null, null)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Validation error").javaClass)
    }
}