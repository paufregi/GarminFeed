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
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.models.User
import paufregi.connectfeed.data.repository.GarminRepository

class RefreshTokensTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: RefreshTokens

    @Before
    fun setup(){
        useCase = RefreshTokens(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Refresh token`() = runTest {
        val user = User("user", "avatar")
        coEvery { repo.deleteTokens() } returns Unit
        coEvery { repo.fetchUser() } returns Result.Success(user)

        val res = useCase()

        assertThat(res).isInstanceOf(Result.Success(Unit).javaClass)
        coVerify {
            repo.deleteTokens()
            repo.fetchUser()
        }
        confirmVerified(repo)
    }

    @Test
    fun `Refresh tokens - failure`() = runTest {
        coEvery { repo.deleteTokens() } returns Unit
        coEvery { repo.fetchUser() } returns Result.Failure<User?>("Error")
        val res = useCase()

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Error").javaClass)
        coVerify {
            repo.deleteTokens()
            repo.fetchUser()
        }
        confirmVerified(repo)
    }
}