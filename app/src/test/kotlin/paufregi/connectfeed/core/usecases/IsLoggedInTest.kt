package paufregi.connectfeed.core.usecases

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.connectfeed.core.models.User
import paufregi.connectfeed.data.repository.GarminRepository

class IsLoggedInTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: IsLoggedIn

    @Before
    fun setup(){
        useCase = IsLoggedIn(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Logged In`() = runTest {
        val user = User("user", "avatar")
        coEvery { repo.getUser() } returns flowOf(user)
        val res = useCase()

        res.test {
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Not logged In`() = runTest {
        coEvery { repo.getUser() } returns flowOf(null)
        val res = useCase()

        res.test {
            assertThat(awaitItem()).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }
}