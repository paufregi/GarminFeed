package paufregi.garminfeed.presentation.quickedit

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.ActivityType
import paufregi.garminfeed.core.models.Course
import paufregi.garminfeed.core.models.EventType
import paufregi.garminfeed.core.models.Profile
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.core.usecases.GetActivitiesUseCase
import paufregi.garminfeed.core.usecases.GetProfilesUseCase
import paufregi.garminfeed.core.usecases.SaveActivityUseCase
import paufregi.garminfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class QuickEditViewModelTest {

    private val getActivities = mockk<GetActivitiesUseCase>()
    private val getProfiles = mockk<GetProfilesUseCase>()
    private val saveActivity = mockk<SaveActivityUseCase>()

    private lateinit var viewModel: QuickEditViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    val activities = listOf(
        Activity(1L, "Running", ActivityType.Running),
        Activity(2L, "Cycling", ActivityType.Cycling)
    )

    val profiles = listOf(
        Profile("profile1", EventType.transportation, ActivityType.Running, Course.home, 1),
        Profile("profile2", EventType.transportation, ActivityType.Cycling, Course.work, 1),
        Profile("profile3", EventType.transportation, ActivityType.Running, Course.home, 1)
    )

    @Before
    fun setup(){

    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Load activities and profiles`() = runTest {
        every { getActivities.invoke() } returns activities
        every { getProfiles.invoke() } returns profiles

        viewModel = QuickEditViewModel(getActivities, getProfiles, saveActivity)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.loading).isFalse()
            assertThat(state.activities).isEqualTo(activities)
            assertThat(state.allProfiles).isEqualTo(profiles)
            assertThat(state.availableProfiles).isEqualTo(profiles)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Select activity`() = runTest {
        every { getActivities.invoke() } returns activities
        every { getProfiles.invoke() } returns profiles

        viewModel = QuickEditViewModel(getActivities, getProfiles, saveActivity)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(QuickEditEvent.SelectActivity(activities[0]))
            val state = awaitItem()
            assertThat(state.selectedActivity).isEqualTo(activities[0])
            assertThat(state.availableProfiles.count()).isEqualTo(2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Select profile`() = runTest {
        every { getActivities.invoke() } returns activities
        every { getProfiles.invoke() } returns profiles

        viewModel = QuickEditViewModel(getActivities, getProfiles, saveActivity)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(QuickEditEvent.SelectProfile(profiles[0]))
            val state = awaitItem()
            assertThat(state.selectedProfile).isEqualTo(profiles[0])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Save activity`() = runTest {
        every { getActivities.invoke() } returns activities
        every { getProfiles.invoke() } returns profiles
        coEvery { saveActivity.invoke(any(), any()) } returns Result.Success(Unit)

        viewModel = QuickEditViewModel(getActivities, getProfiles, saveActivity)

        viewModel.state.test {
            viewModel.onEvent(QuickEditEvent.SelectActivity(activities[0]))
            viewModel.onEvent(QuickEditEvent.SelectProfile(profiles[0]))
            viewModel.onEvent(QuickEditEvent.Save)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { saveActivity.invoke(activities[0], profiles[0]) }
        confirmVerified( saveActivity )
    }
}