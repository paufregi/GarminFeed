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

class SetupDoneTest {
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: SetupDone

    @Before
    fun setup() {
        useCase = SetupDone(repo)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `Setup done`() = runTest {
        coEvery { repo.saveSetup(any()) } returns Unit

        useCase()

        coVerify { repo.saveSetup(true) }
        confirmVerified(repo)
    }
}