package paufregi.connectfeed.core.usecases

import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.models.User
import paufregi.connectfeed.data.repository.GarminRepository

class ChangePasswordTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: ChangePassword

    @Before
    fun setup(){
        useCase = ChangePassword(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Change password`() = runTest {
        val oldCredential = Credential("user", "oldPass")
        val user = User("user", "avatar")
        coEvery { repo.getCredential() } returns flowOf(oldCredential)
        coEvery { repo.deleteTokens() } returns Unit
        coEvery { repo.saveCredential(any()) } returns Unit
        coEvery { repo.fetchUser() } returns Result.Success(user)

        val res = useCase("newPass")

        assertThat(res).isInstanceOf(Result.Success(Unit).javaClass)
        coVerify {
            repo.getCredential()
            repo.deleteTokens()
            repo.saveCredential(Credential("user", "newPass"))
            repo.fetchUser()
        }
        confirmVerified(repo)
    }

    @Test
    fun `Wrong password`() = runTest {
        val oldCredential = Credential("user", "oldPass")
        coEvery { repo.getCredential() } returns flowOf(oldCredential)
        coEvery { repo.deleteTokens() } returns Unit
        coEvery { repo.saveCredential(any()) } returns Unit
        coEvery { repo.fetchUser() } returns Result.Failure("Wrong password")

        val res = useCase("newPass")

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Wrong password").javaClass)
        coVerify {
            repo.getCredential()
            repo.deleteTokens()
            repo.saveCredential(Credential("user", "newPass"))
            repo.fetchUser()
            repo.deleteTokens()
            repo.saveCredential(Credential("user", "oldPass"))

        }
        confirmVerified(repo)
    }

    @Test
    fun `Blank password`() = runTest {
        val res = useCase("")

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Validation error").javaClass)
    }

}