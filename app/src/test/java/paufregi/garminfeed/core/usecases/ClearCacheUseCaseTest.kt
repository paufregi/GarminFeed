package paufregi.garminfeed.core.usecases

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import paufregi.garminfeed.data.repository.GarminRepository

class ClearCacheUseCaseTest {

    private val repo = mockk<GarminRepository>()
    private val useCase = ClearCacheUseCase(repo)

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Clear cache use-case`() = runTest{
        coEvery { repo.clearCache() } returns Unit
        useCase()
        coVerify { repo.clearCache() }
        confirmVerified(repo)
    }
}