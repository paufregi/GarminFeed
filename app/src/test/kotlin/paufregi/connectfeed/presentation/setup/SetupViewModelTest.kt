package paufregi.connectfeed.presentation.setup

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.SetupDone
import paufregi.connectfeed.core.usecases.SignIn
import paufregi.connectfeed.presentation.ui.models.ProcessState
import paufregi.connectfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class SetupViewModelTest {

    private val signIn = mockk<SignIn>()
    private val setupDone = mockk<SetupDone>()

    private lateinit var viewModel: SetupViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup(){}

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Set username`() = runTest {
        viewModel = SetupViewModel(signIn, setupDone)

        viewModel.state.test {
            assertThat(awaitItem().credential.username).isEqualTo("")
            viewModel.onEvent(SetupEvent.SetUsername("user"))
            assertThat(awaitItem().credential.username).isEqualTo("user")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set password`() = runTest {
        viewModel = SetupViewModel(signIn, setupDone)

        viewModel.state.test {
            assertThat(awaitItem().credential.password).isEqualTo("")
            viewModel.onEvent(SetupEvent.SetPassword("pass"))
            assertThat(awaitItem().credential.password).isEqualTo("pass")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Show password`() = runTest {
        viewModel = SetupViewModel(signIn, setupDone)

        viewModel.state.test {
            assertThat(awaitItem().showPassword).isFalse()
            viewModel.onEvent(SetupEvent.ShowPassword(true))
            assertThat(awaitItem().showPassword).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Reset state`() = runTest {
        viewModel = SetupViewModel(signIn, setupDone)
        viewModel.onEvent(SetupEvent.SetUsername("user"))
        viewModel.onEvent(SetupEvent.SetPassword("pass"))
        viewModel.onEvent(SetupEvent.ShowPassword(true))

        viewModel.state.test {
            var state = awaitItem()
            assertThat(state.credential.username).isEqualTo("user")
            assertThat(state.credential.password).isEqualTo("pass")
            assertThat(state.showPassword).isTrue()
            viewModel.onEvent(SetupEvent.Reset)
            state = awaitItem()
            assertThat(state.credential.username).isEqualTo("")
            assertThat(state.credential.password).isEqualTo("")
            assertThat(state.showPassword).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Sign in - success`() = runTest {
        coEvery { signIn(any()) } returns Result.Success("Paul")
        viewModel = SetupViewModel(signIn, setupDone)
        viewModel.onEvent(SetupEvent.SetUsername("user"))
        viewModel.onEvent(SetupEvent.SetPassword("pass"))

        viewModel.state.test {
            assertThat(awaitItem().process).isEqualTo(ProcessState.Idle)
            viewModel.onEvent(SetupEvent.SignIn)
            assertThat(awaitItem().process).isEqualTo(ProcessState.Success("Paul"))
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { signIn(Credential("user", "pass")) }
        confirmVerified(signIn, setupDone)
    }

    @Test
    fun `Sign in - failed`() = runTest {
        coEvery { signIn(any()) } returns Result.Failure("error")
        viewModel = SetupViewModel(signIn, setupDone)
        viewModel.onEvent(SetupEvent.SetUsername("user"))
        viewModel.onEvent(SetupEvent.SetPassword("pass"))

        viewModel.state.test {
            assertThat(awaitItem().process).isEqualTo(ProcessState.Idle)
            viewModel.onEvent(SetupEvent.SignIn)
            assertThat(awaitItem().process).isEqualTo(ProcessState.Failure("error"))
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { signIn(Credential("user", "pass")) }
        confirmVerified(signIn, setupDone)
    }

    @Test
    fun `Setup done`() = runTest {
        coEvery { setupDone() } returns Unit
        viewModel = SetupViewModel(signIn, setupDone)

        viewModel.onEvent(SetupEvent.Done)

        coVerify { setupDone() }
        confirmVerified(signIn, setupDone)
    }
}