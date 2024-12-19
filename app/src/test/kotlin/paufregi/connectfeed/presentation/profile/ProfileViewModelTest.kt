package paufregi.connectfeed.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.GetActivityTypes
import paufregi.connectfeed.core.usecases.GetCourses
import paufregi.connectfeed.core.usecases.GetEventTypes
import paufregi.connectfeed.core.usecases.GetProfile
import paufregi.connectfeed.core.usecases.SaveProfile
import paufregi.connectfeed.presentation.Route
import paufregi.connectfeed.presentation.ui.components.ProcessState
import paufregi.connectfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private val getProfile = mockk<GetProfile>()
    private val getActivityTypes = mockk<GetActivityTypes>()
    private val getEventTypes = mockk<GetEventTypes>()
    private val getCourses = mockk<GetCourses>()
    private val saveProfile = mockk<SaveProfile>()
    private val savedState = mockk<SavedStateHandle>()

    private lateinit var viewModel: ProfileViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup(){
        mockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Load data`() = runTest {
        val profile = Profile(id = 1, name = "profile")
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns profile
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processState).isEqualTo(ProcessState.Idle)
            assertThat(state.profile).isEqualTo(profile)
            assertThat(state.activityTypes).isEqualTo(activityTypes)
            assertThat(state.eventTypes).isEqualTo(eventTypes)
            assertThat(state.courses).isEqualTo(courses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Load data - new profile`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processState).isEqualTo(ProcessState.Idle)
            assertThat(state.profile).isEqualTo(Profile())
            assertThat(state.activityTypes).isEqualTo(activityTypes)
            assertThat(state.eventTypes).isEqualTo(eventTypes)
            assertThat(state.courses).isEqualTo(courses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Fails to load event types`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Failure<List<EventType>>("error")
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processState).isEqualTo(ProcessState.Failure("Couldn't load event types"))
            assertThat(state.profile).isEqualTo(Profile())
            assertThat(state.activityTypes).isEqualTo(activityTypes)
            assertThat(state.eventTypes).isEqualTo(emptyList<EventType>())
            assertThat(state.courses).isEqualTo(courses)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Fails to load courses`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Failure<List<Course>>("error")

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processState).isEqualTo(ProcessState.Failure("Couldn't load courses"))
            assertThat(state.profile).isEqualTo(Profile())
            assertThat(state.activityTypes).isEqualTo(activityTypes)
            assertThat(state.eventTypes).isEqualTo(eventTypes)
            assertThat(state.courses).isEqualTo(emptyList<Course>())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Fails to load event types and courses`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Failure<List<EventType>>("error")
        coEvery { getCourses.invoke() } returns Result.Failure<List<Course>>("error")

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processState).isEqualTo(ProcessState.Failure("Couldn't load event types & courses"))
            assertThat(state.profile).isEqualTo(Profile())
            assertThat(state.activityTypes).isEqualTo(activityTypes)
            assertThat(state.eventTypes).isEqualTo(emptyList<EventType>())
            assertThat(state.courses).isEqualTo(emptyList<Course>())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set name`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.SetName("name"))
            val state = awaitItem()
            assertThat(state.profile.name).isEqualTo("name")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set activity type`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.SetActivityType(ActivityType.Running))
            val state = awaitItem()
            assertThat(state.profile.activityType).isEqualTo(ActivityType.Running)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set activity type - with course`() = runTest {
        val course = Course(id = 1, name = "course", type = ActivityType.Running)
        val profile = Profile(id = 1, name = "profile", course = course)
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns profile
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.SetActivityType(ActivityType.Running))
            val state = awaitItem()
            assertThat(state.profile.activityType).isEqualTo(ActivityType.Running)
            assertThat(state.profile.course).isEqualTo(course)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set activity type - unset course`() = runTest {
        val course = Course(id = 1, name = "course", type = ActivityType.Running)
        val profile = Profile(id = 1, name = "profile", course = course)
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns profile
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.SetActivityType(ActivityType.Cycling))
            val state = awaitItem()
            assertThat(state.profile.activityType).isEqualTo(ActivityType.Cycling)
            assertThat(state.profile.course).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set event type`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))
        val eventType = EventType(id = 1, name = "event")

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.SetEventType(eventType))
            val state = awaitItem()
            assertThat(state.profile.eventType).isEqualTo(eventType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set course`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))
        val course = Course(id = 1, name = "course", type = ActivityType.Running)

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.SetCourse(course))
            val state = awaitItem()
            assertThat(state.profile.course).isEqualTo(course)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set water`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.SetWater(100))
            val state = awaitItem()
            assertThat(state.profile.water).isEqualTo(100)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set rename`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.SetRename(false))
            val state = awaitItem()
            assertThat(state.profile.rename).isEqualTo(false)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set custom water`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.SetCustomWater(true))
            val state = awaitItem()
            assertThat(state.profile.customWater).isEqualTo(true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Set feel and effort`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.SetFeelAndEffort(true))
            val state = awaitItem()
            assertThat(state.profile.feelAndEffort).isEqualTo(true)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `Save activity`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)
        coEvery { saveProfile.invoke(any()) } returns Result.Success(Unit)

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.Save)
            val state = awaitItem()
            assertThat(state.processState).isEqualTo(ProcessState.Success("Profile updated"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Save profile - failure`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Route.Profile>() } returns Route.Profile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)
        coEvery { saveProfile.invoke(any()) } returns Result.Failure("error")

        viewModel = ProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(ProfileEvent.Save)
            val state = awaitItem()
            assertThat(state.processState).isEqualTo(ProcessState.Failure("error"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}