package paufregi.connectfeed.presentation.login

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
import paufregi.connectfeed.core.models.User
import paufregi.connectfeed.core.usecases.SignIn
import paufregi.connectfeed.presentation.ui.models.ProcessState
import paufregi.connectfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private val signIn = mockk<SignIn>()

    private lateinit var viewModel: LoginViewModel

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
        viewModel = LoginViewModel(signIn)

        viewModel.state.test {
            assertThat(awaitItem().credential.username).isEqualTo("")
            viewModel.onEvent(LoginEvent.SetUsername("user"))
            assertThat(awaitItem().credential.username).isEqualTo("user")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set password`() = runTest {
        viewModel = LoginViewModel(signIn)

        viewModel.state.test {
            assertThat(awaitItem().credential.password).isEqualTo("")
            viewModel.onEvent(LoginEvent.SetPassword("pass"))
            assertThat(awaitItem().credential.password).isEqualTo("pass")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Show password`() = runTest {
        viewModel = LoginViewModel(signIn)

        viewModel.state.test {
            assertThat(awaitItem().showPassword).isFalse()
            viewModel.onEvent(LoginEvent.ShowPassword(true))
            assertThat(awaitItem().showPassword).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Reset state`() = runTest {
        viewModel = LoginViewModel(signIn)
        viewModel.onEvent(LoginEvent.SetUsername("user"))
        viewModel.onEvent(LoginEvent.SetPassword("pass"))
        viewModel.onEvent(LoginEvent.ShowPassword(true))

        viewModel.state.test {
            var state = awaitItem()
            assertThat(state.credential.username).isEqualTo("user")
            assertThat(state.credential.password).isEqualTo("pass")
            assertThat(state.showPassword).isTrue()
            viewModel.onEvent(LoginEvent.Reset)
            state = awaitItem()
            assertThat(state.credential.username).isEqualTo("")
            assertThat(state.credential.password).isEqualTo("")
            assertThat(state.showPassword).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Sign in - success`() = runTest {
        val user = User("user", "avatar")
        coEvery { signIn(any()) } returns Result.Success(user)
        viewModel = LoginViewModel(signIn)
        viewModel.onEvent(LoginEvent.SetUsername("user"))
        viewModel.onEvent(LoginEvent.SetPassword("pass"))

        viewModel.state.test {
            assertThat(awaitItem().process).isEqualTo(ProcessState.Idle)
            viewModel.onEvent(LoginEvent.SignIn)
            assertThat(awaitItem().process).isEqualTo(ProcessState.Success("user"))
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { signIn(Credential("user", "pass")) }
        confirmVerified(signIn)
    }

    @Test
    fun `Sign in - failed`() = runTest {
        coEvery { signIn(any()) } returns Result.Failure("error")
        viewModel = LoginViewModel(signIn)
        viewModel.onEvent(LoginEvent.SetUsername("user"))
        viewModel.onEvent(LoginEvent.SetPassword("pass"))

        viewModel.state.test {
            assertThat(awaitItem().process).isEqualTo(ProcessState.Idle)
            viewModel.onEvent(LoginEvent.SignIn)
            assertThat(awaitItem().process).isEqualTo(ProcessState.Failure("error"))
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { signIn(Credential("user", "pass")) }
        confirmVerified(signIn)
    }
}