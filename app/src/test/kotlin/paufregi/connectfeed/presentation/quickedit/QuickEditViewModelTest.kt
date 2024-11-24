package paufregi.connectfeed.presentation.quickedit

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
import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.GetLatestActivitiesUseCase
import paufregi.connectfeed.core.usecases.GetProfilesUseCase
import paufregi.connectfeed.core.usecases.UpdateActivityUseCase
import paufregi.connectfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class QuickEditViewModelTest {

    private val getActivities = mockk<GetLatestActivitiesUseCase>()
    private val getProfiles = mockk<GetProfilesUseCase>()
    private val updateActivity = mockk<UpdateActivityUseCase>()

    private lateinit var viewModel: QuickEditViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    val activities = listOf(
        Activity(1L, "Running", ActivityType.Running),
        Activity(2L, "Cycling", ActivityType.Cycling)
    )

    val profiles = listOf(
        Profile(name = "profile1", rename = true, eventType = EventType.transportation, activityType = ActivityType.Running, course = Course.commuteHome, water = 1),
        Profile(name = "profile2", rename = true, eventType = EventType.transportation, activityType = ActivityType.Cycling, course = Course.commuteWork, water = 1),
        Profile(name = "profile3", rename = true, eventType = EventType.transportation, activityType = ActivityType.Running, course = Course.commuteHome, water = 1)
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
        coEvery { getActivities.invoke() } returns Result.Success(activities)
        every { getProfiles.invoke() } returns flowOf(profiles)

        viewModel = QuickEditViewModel(getActivities, getProfiles, updateActivity)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processing).isEqualTo(ProcessState.Idle)
            assertThat(state.activities).isEqualTo(activities)
            assertThat(state.allProfiles).isEqualTo(profiles)
            assertThat(state.availableProfiles).isEqualTo(profiles)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Fails to load activities`() = runTest {
        coEvery { getActivities.invoke() } returns Result.Failure("error")
        every { getProfiles.invoke() } returns flowOf(profiles)

        viewModel = QuickEditViewModel(getActivities, getProfiles, updateActivity)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processing).isEqualTo(ProcessState.FailureLoading)
            assertThat(state.activities).isEqualTo(emptyList<Activity>())
            assertThat(state.allProfiles).isEqualTo(profiles)
            assertThat(state.availableProfiles).isEqualTo(profiles)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Select activity`() = runTest {
        coEvery { getActivities.invoke() } returns Result.Success(activities)
        every { getProfiles.invoke() } returns flowOf(profiles)

        viewModel = QuickEditViewModel(getActivities, getProfiles, updateActivity)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(QuickEditEvent.SetActivity(activities[0]))
            val state = awaitItem()
            assertThat(state.activity).isEqualTo(activities[0])
            assertThat(state.availableProfiles.count()).isEqualTo(2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Select effort`() = runTest {
        coEvery { getActivities.invoke() } returns Result.Success(activities)
        every { getProfiles.invoke() } returns flowOf(profiles)

        viewModel = QuickEditViewModel(getActivities, getProfiles, updateActivity)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(QuickEditEvent.SetEffort(50f))
            val state = awaitItem()
            assertThat(state.effort).isEqualTo(50f)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Select feel`() = runTest {
        coEvery { getActivities.invoke() } returns Result.Success(activities)
        every { getProfiles.invoke() } returns flowOf(profiles)

        viewModel = QuickEditViewModel(getActivities, getProfiles, updateActivity)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(QuickEditEvent.SetFeel(50f))
            val state = awaitItem()
            assertThat(state.feel).isEqualTo(50f)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Select profile`() = runTest {
        coEvery { getActivities.invoke() } returns Result.Success(activities)
        every { getProfiles.invoke() } returns flowOf(profiles)

        viewModel = QuickEditViewModel(getActivities, getProfiles, updateActivity)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(QuickEditEvent.SetProfile(profiles[0]))
            val state = awaitItem()
            assertThat(state.profile).isEqualTo(profiles[0])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Save activity`() = runTest {
        coEvery { getActivities.invoke() } returns Result.Success(activities)
        every { getProfiles.invoke() } returns flowOf(profiles)
        coEvery { updateActivity.invoke(any(), any(), any(), any()) } returns Result.Success(Unit)

        viewModel = QuickEditViewModel(getActivities, getProfiles, updateActivity)

        viewModel.state.test {
            viewModel.onEvent(QuickEditEvent.SetActivity(activities[0]))
            viewModel.onEvent(QuickEditEvent.SetProfile(profiles[0]))
            viewModel.onEvent(QuickEditEvent.SetFeel(50F))
            viewModel.onEvent(QuickEditEvent.SetEffort(80f))
            viewModel.onEvent(QuickEditEvent.Save)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { updateActivity.invoke(activities[0], profiles[0], 50f, 80f) }
        confirmVerified( updateActivity )
    }
}