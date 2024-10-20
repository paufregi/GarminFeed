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
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.data.repository.GarminRepository

class SaveCredentialUseCaseTest{
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: SaveCredentialUseCase

    @Before
    fun setup(){
        useCase = SaveCredentialUseCase(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Save credential use-case`() = runTest{
        val credential = Credential("user", "pass")
        coEvery { repo.saveCredential(any()) } returns Unit
        val res = useCase(credential)

        assertThat(res).isInstanceOf(Result.Success(Unit).javaClass)
        coVerify { repo.saveCredential(credential) }
        confirmVerified(repo)
    }

    @Test
    fun `Invalid - No username`() = runTest{
        val credential = Credential("", "pass")
        val res = useCase(credential)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Validation error").javaClass)
    }

    @Test
    fun `Invalid - No pass`() = runTest{
        val credential = Credential("user", "")
        val res = useCase(credential)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Validation error").javaClass)
    }

    @Test
    fun `Invalid - all blank`() = runTest{
        val credential = Credential("", "")
        val res = useCase(credential)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Validation error").javaClass)
    }
}