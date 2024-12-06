package paufregi.connectfeed.presentation.settings

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.GetCredential
import paufregi.connectfeed.core.usecases.SaveCredential
import paufregi.connectfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    private val getCredential = mockk<GetCredential>()
    private val saveCredential = mockk<SaveCredential>()

    private lateinit var viewModel: SettingsViewModel

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
    fun `Update username`() = runTest {
        every { getCredential.invoke() } returns flowOf(Credential("", ""))

        viewModel = SettingsViewModel(getCredential, saveCredential)

        viewModel.state.test {
            assertThat(awaitItem().credential.username).isEqualTo("")
            viewModel.onEvent(SettingsEvent.SetUsername("user"))
            assertThat(awaitItem().credential.username).isEqualTo("user")
            viewModel.onEvent(SettingsEvent.SetUsername("user2"))
            assertThat(awaitItem().credential.username).isEqualTo("user2")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Update password`() = runTest {
        every { getCredential.invoke() } returns flowOf(Credential("", ""))

        viewModel = SettingsViewModel(getCredential, saveCredential)

        viewModel.state.test {
            assertThat(awaitItem().credential.password).isEqualTo("")
            viewModel.onEvent(SettingsEvent.SetPassword("password"))
            assertThat(awaitItem().credential.password).isEqualTo("password")
            viewModel.onEvent(SettingsEvent.SetPassword("password2"))
            assertThat(awaitItem().credential.password).isEqualTo("password2")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Update show password`() = runTest {
        every { getCredential.invoke() } returns flowOf(Credential("", ""))

        viewModel = SettingsViewModel(getCredential, saveCredential)

        viewModel.state.test {
            assertThat(awaitItem().showPassword).isFalse()
            viewModel.onEvent(SettingsEvent.SetShowPassword(true))
            assertThat(awaitItem().showPassword).isTrue()
            viewModel.onEvent(SettingsEvent.SetShowPassword(false))
            assertThat(awaitItem().showPassword).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Save credential`() = runTest {
        val cred = Credential("saveUser", "savePass")
        every { getCredential.invoke() } returns flowOf(Credential("", ""))

        viewModel = SettingsViewModel(getCredential, saveCredential)

        coEvery { saveCredential.invoke(any()) } returns Result.Success(Unit)

        viewModel.state.test{
            viewModel.onEvent(SettingsEvent.SetUsername(cred.username))
            viewModel.onEvent(SettingsEvent.SetPassword(cred.password))
            viewModel.onEvent(SettingsEvent.Save)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { saveCredential.invoke(cred) }
        confirmVerified( saveCredential )
    }
}