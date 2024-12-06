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
import paufregi.connectfeed.core.usecases.GetLatestActivities
import paufregi.connectfeed.core.usecases.GetProfiles
import paufregi.connectfeed.core.usecases.UpdateActivity
import paufregi.connectfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class QuickEditViewModelTest {

    private val getActivities = mockk<GetLatestActivities>()
    private val getProfiles = mockk<GetProfiles>()
    private val updateActivity = mockk<UpdateActivity>()

    private lateinit var viewModel: QuickEditViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    val activities = listOf(
        Activity(1L, "Running", ActivityType.Running),
        Activity(2L, "Cycling", ActivityType.Cycling)
    )

    val profiles = listOf(
        Profile(name = "profile1", activityType = ActivityType.Running),
        Profile(name = "profile2" ,activityType = ActivityType.Cycling),
        Profile(name = "profile3", activityType = ActivityType.Running)
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
            assertThat(state.profiles).isEqualTo(profiles)
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
            assertThat(state.profiles).isEqualTo(profiles)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set activity`() = runTest {
        coEvery { getActivities.invoke() } returns Result.Success(activities)
        every { getProfiles.invoke() } returns flowOf(profiles)

        viewModel = QuickEditViewModel(getActivities, getProfiles, updateActivity)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(QuickEditEvent.SetActivity(activities[0]))
            val state = awaitItem()
            assertThat(state.activity).isEqualTo(activities[0])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set activity with matching profile`() = runTest {
        coEvery { getActivities.invoke() } returns Result.Success(activities)
        every { getProfiles.invoke() } returns flowOf(profiles)

        viewModel = QuickEditViewModel(getActivities, getProfiles, updateActivity)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(QuickEditEvent.SetProfile(profiles[0]))
            awaitItem() // Skip this state
            viewModel.onEvent(QuickEditEvent.SetActivity(activities[0]))
            val state = awaitItem()
            assertThat(state.activity).isEqualTo(activities[0])
            assertThat(state.profile).isEqualTo(profiles[0])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set activity with no matching profile`() = runTest {
        coEvery { getActivities.invoke() } returns Result.Success(activities)
        every { getProfiles.invoke() } returns flowOf(profiles)

        viewModel = QuickEditViewModel(getActivities, getProfiles, updateActivity)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(QuickEditEvent.SetProfile(profiles[1]))
            awaitItem() // Skip this state
            viewModel.onEvent(QuickEditEvent.SetActivity(activities[0]))
            val state = awaitItem()
            assertThat(state.activity).isEqualTo(activities[0])
            assertThat(state.profile).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set effort`() = runTest {
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
    fun `Set feel`() = runTest {
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
    fun `Set profile`() = runTest {
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