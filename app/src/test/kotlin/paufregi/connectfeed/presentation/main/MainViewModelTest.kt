package paufregi.connectfeed.presentation.main

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.connectfeed.core.usecases.IsSetupDone
import paufregi.connectfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private val isSetupDone = mockk<IsSetupDone>()

    private lateinit var viewModel: MainViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup(){

    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Load data`() = runTest {

        every { isSetupDone.invoke() } returns flowOf(true)

        viewModel = MainViewModel(isSetupDone)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state).isEqualTo(true)
            cancelAndIgnoreRemainingEvents()
        }
    }
}