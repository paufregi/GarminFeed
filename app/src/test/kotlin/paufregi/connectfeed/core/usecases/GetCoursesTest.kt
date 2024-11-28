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
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository

class GetCoursesTest {

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
    fun `Get courses`() = runTest {
        val courses = listOf(
            Course(id = 1, name = "course 1", type = ActivityType.Running),
            Course(id = 2, name = "course 2", type = ActivityType.Cycling),
        )
        coEvery { repo.getCourses() } returns Result.Success(courses)
        val res = useCase()

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(courses)
        coVerify { repo.getCourses() }
        confirmVerified(repo)
    }

    @Test
    fun `Get courses - failure`() = runTest {
        coEvery { repo.getCourses() } returns Result.Failure("Failed")
        val res = useCase()

        assertThat(res.isSuccessful).isFalse()
        coVerify { repo.getCourses() }
        confirmVerified(repo)
    }
}