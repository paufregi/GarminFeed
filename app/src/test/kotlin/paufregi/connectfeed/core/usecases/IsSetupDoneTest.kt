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
import paufregi.connectfeed.data.repository.GarminRepository

class IsSetupDoneTest {
    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: IsSetupDone

    @Before
    fun setup() {
        useCase = IsSetupDone(repo)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `Setup done`() = runTest {
        coEvery { repo.getSetup() } returns flowOf(true)

        useCase().test {
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repo.getSetup() }
        confirmVerified(repo)
    }

    @Test
    fun `Setup not done`() = runTest {
        coEvery { repo.getSetup() } returns flowOf(false)

        useCase().test {
            assertThat(awaitItem()).isFalse()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repo.getCredential() }
        confirmVerified(repo)
    }
}