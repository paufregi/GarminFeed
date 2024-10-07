package paufregi.garminfeed.core.usecases

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
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.data.repository.GarminRepository

class GetCredentialUseCaseTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: GetCredentialUseCase

    @Before
    fun setUp(){
        useCase = GetCredentialUseCase(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Get credentials use-case`() = runTest{
        val credential = Credential("user", "pass")
        coEvery { repo.getCredential() } returns credential
        val res = useCase()

        assertThat(res).isEqualTo(credential)
        coVerify { repo.getCredential() }
        confirmVerified(repo)
    }

    @Test
    fun `No credentials use-case`() = runTest{
        coEvery { repo.getCredential() } returns null
        val res = useCase()

        assertThat(res).isNull()
        coVerify { repo.getCredential() }
        confirmVerified(repo)
    }
}