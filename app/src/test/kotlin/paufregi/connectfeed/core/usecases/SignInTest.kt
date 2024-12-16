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
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository

class SignInTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: SignIn

    @Before
    fun setup(){
        useCase = SignIn(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `SignIn - success`() = runTest {
        val credential = Credential("user", "pass")
        coEvery { repo.saveCredential(any()) } returns Unit
        coEvery { repo.fetchFullName() } returns Result.Success("ConnectFeed")
        val res = useCase(credential)

        assertThat(res).isInstanceOf(Result.Success<String>("ConnectFeed").javaClass)
        coVerify { repo.saveCredential(credential) }
        coVerify { repo.fetchFullName() }
        confirmVerified(repo)
    }

    @Test
    fun `SignIn - failure`() = runTest {
        val credential = Credential("user", "pass")
        coEvery { repo.saveCredential(any()) } returns Unit
        coEvery { repo.fetchFullName() } returns Result.Failure("error")
        coEvery { repo.deleteCredential() } returns Unit
        coEvery { repo.deleteTokens() } returns Unit
        val res = useCase(credential)

        assertThat(res).isInstanceOf(Result.Failure<String>("error").javaClass)
        coVerify { repo.saveCredential(credential) }
        coVerify { repo.fetchFullName() }
        coVerify { repo.deleteCredential() }
        coVerify { repo.deleteTokens() }
        confirmVerified(repo)
    }

    @Test
    fun `Invalid - No username`() = runTest {
        val credential = Credential("", "pass")
        val res = useCase(credential)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Validation error").javaClass)
        confirmVerified(repo)
    }

    @Test
    fun `Invalid - No pass`() = runTest {
        val credential = Credential("user", "")
        val res = useCase(credential)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Validation error").javaClass)
        confirmVerified(repo)
    }

    @Test
    fun `Invalid - all blank`() = runTest {
        val credential = Credential("", "")
        val res = useCase(credential)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Validation error").javaClass)
        confirmVerified(repo)
    }
}