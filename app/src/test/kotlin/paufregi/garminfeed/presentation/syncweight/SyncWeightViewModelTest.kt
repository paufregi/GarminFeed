package paufregi.garminfeed.presentation.syncweight

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.core.usecases.ClearCacheUseCase
import paufregi.garminfeed.core.usecases.GetCredentialUseCase
import paufregi.garminfeed.core.usecases.SaveCredentialUseCase
import paufregi.garminfeed.presentation.home.HomeEvent
import paufregi.garminfeed.presentation.home.HomeViewModel
import paufregi.garminfeed.presentation.settings.SettingsEvent
import paufregi.garminfeed.presentation.settings.SettingsViewModel
import paufregi.garminfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class SyncWeightViewModelTest {

    private val getCredential = mockk<GetCredentialUseCase>()
    private val saveCredential = mockk<SaveCredentialUseCase>()

    private lateinit var viewModel: SettingsViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp(){
        viewModel = SettingsViewModel(getCredential, saveCredential)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Update username`() = runTest {
        coEvery { getCredential.invoke() } returns Credential("", "")
        viewModel.state.test {
            assertThat(awaitItem().credential.username).isEqualTo("")
            viewModel.onEvent(SettingsEvent.UpdateUsername("user"))
            assertThat(awaitItem().credential.username).isEqualTo("user")
            viewModel.onEvent(SettingsEvent.UpdateUsername("user2"))
            assertThat(awaitItem().credential.username).isEqualTo("user2")
        }
    }

    @Test
    fun `Update password`() = runTest {
        coEvery { getCredential.invoke() } returns Credential("", "")
        viewModel.state.test {
            assertThat(awaitItem().credential.password).isEqualTo("")
            viewModel.onEvent(SettingsEvent.UpdatePassword("password"))
            assertThat(awaitItem().credential.password).isEqualTo("password")
            viewModel.onEvent(SettingsEvent.UpdatePassword("password2"))
            assertThat(awaitItem().credential.password).isEqualTo("password2")
        }
    }

    @Test
    fun `Update show password`() = runTest {
        coEvery { getCredential.invoke() } returns Credential("", "")
        viewModel.state.test {
            assertThat(awaitItem().showPassword).isFalse()
            viewModel.onEvent(SettingsEvent.UpdateShowPassword(true))
            assertThat(awaitItem().showPassword).isTrue()
            viewModel.onEvent(SettingsEvent.UpdateShowPassword(false))
            assertThat(awaitItem().showPassword).isFalse()
        }
    }
}