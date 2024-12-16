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

class ClearCacheTest {

    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: ClearCache

    @Before
    fun setup(){
        useCase = ClearCache(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Clear cache`() = runTest {
        coEvery { repo.deleteTokens() } returns Unit
        useCase()
        coVerify { repo.deleteTokens() }
        confirmVerified(repo)
    }
}