package paufregi.connectfeed.core.usecases

import app.cash.turbine.test
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
import paufregi.connectfeed.data.repository.GarminRepository

class GetCredentialTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: GetCredential

    @Before
    fun setup(){
        useCase = GetCredential(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Get credential`() = runTest {
        val credential = Credential("user", "pass")
        coEvery { repo.getCredential() } returns flowOf(credential)
        val res = useCase()

        res.test {
            assertThat(awaitItem()).isEqualTo(credential)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repo.getCredential() }
        confirmVerified(repo)
    }

    @Test
    fun `No credential`() = runTest {
        coEvery { repo.getCredential() } returns flowOf(null)
        val res = useCase()

        res.test {
            assertThat(awaitItem()).isNull()
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { repo.getCredential() }
        confirmVerified(repo)
    }
}