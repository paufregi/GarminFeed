package paufregi.connectfeed.presentation.settings

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.core.usecases.DeleteProfile
import paufregi.connectfeed.core.usecases.GetProfiles
import paufregi.connectfeed.presentation.profiles.ProfileEvent
import paufregi.connectfeed.presentation.profiles.ProfilesViewModel
import paufregi.connectfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class ProfilesViewModelTest {

    private val getProfiles = mockk<GetProfiles>()
    private val deleteProfile = mockk<DeleteProfile>()

    private lateinit var viewModel: ProfilesViewModel

    private val profiles = listOf(
        Profile(id = 1, name = "Profile 1"),
        Profile(id = 2, name = "Profile 2"),
    )

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
        every { getProfiles.invoke() } returns flowOf(profiles)

        viewModel = ProfilesViewModel(getProfiles, deleteProfile)

        viewModel.state.test {
            assertThat(awaitItem().profiles).isEqualTo(profiles)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Load data - empty list`() = runTest {
        every { getProfiles.invoke() } returns flowOf(emptyList())

        viewModel = ProfilesViewModel(getProfiles, deleteProfile)

        viewModel.state.test {
            assertThat(awaitItem().profiles).isEqualTo(emptyList<Profile>())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Delete profile`() = runTest {
        every { getProfiles.invoke() } returns flowOf(profiles)
        coEvery { deleteProfile.invoke(any()) } returns Unit

        viewModel = ProfilesViewModel(getProfiles, deleteProfile)

        viewModel.state.test {
            assertThat(awaitItem().profiles).isEqualTo(profiles)
            viewModel.onEvent(ProfileEvent.Delete(profiles[0]))
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { deleteProfile.invoke(profiles[0]) }
    }
}