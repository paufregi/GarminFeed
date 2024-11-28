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
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.data.repository.GarminRepository

class DeleteProfileTest {

    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: DeleteProfile

    @Before
    fun setup(){
        useCase = DeleteProfile(repo)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Delete profile`() = runTest {
        coEvery { repo.deleteProfile(any()) } returns Unit
        val profile = Profile()
        useCase(profile)
        coVerify { repo.deleteProfile(profile) }
        confirmVerified(repo)
    }
}