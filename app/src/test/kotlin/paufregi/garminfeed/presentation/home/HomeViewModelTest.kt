package paufregi.garminfeed.presentation.home

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.garminfeed.core.usecases.ClearCacheUseCase
import paufregi.garminfeed.core.usecases.GetCredentialUseCase
import paufregi.garminfeed.core.usecases.GetSetupDoneUseCase
import paufregi.garminfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val getSetupDone = mockk<GetSetupDoneUseCase>()
    private val clearCache = mockk<ClearCacheUseCase>()

    private lateinit var viewModel: HomeViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Clear cache`() = runTest {
        every { getSetupDone.invoke() } returns flowOf(true)
        coEvery { clearCache.invoke() } returns Unit

        viewModel = HomeViewModel(getSetupDone, clearCache)

        viewModel.onEvent(HomeEvent.CleanCache)

        verify { getSetupDone.invoke() }
        coVerify { clearCache.invoke() }
        confirmVerified(clearCache)
    }
}