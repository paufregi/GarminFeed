package paufregi.connectfeed.core.usecases

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.connectfeed.data.repository.GarminRepository

class SignOutTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: SignOut

    @Before
    fun setup(){
        useCase = SignOut(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Sign out`() = runTest {
        coEvery { repo.deleteCredential() } returns Unit
        coEvery { repo.deleteTokens() } returns Unit
        val res = useCase()

        coVerify {
            repo.deleteCredential()
            repo.deleteTokens()
        }
        confirmVerified(repo)
    }
}