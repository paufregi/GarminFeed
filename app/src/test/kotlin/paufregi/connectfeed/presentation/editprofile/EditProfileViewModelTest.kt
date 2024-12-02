package paufregi.connectfeed.presentation.editprofile

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.clearStaticMockk
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
import paufregi.connectfeed.presentation.Routes
import paufregi.connectfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class EditProfileViewModelTest {

    private val getProfile = mockk<GetProfile>()
    private val getActivityTypes = mockk<GetActivityTypes>()
    private val getEventTypes = mockk<GetEventTypes>()
    private val getCourses = mockk<GetCourses>()
    private val saveProfile = mockk<SaveProfile>()
    private val savedState = mockk<SavedStateHandle>()

    private lateinit var viewModel: EditProfileViewModel

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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns profile
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processing).isEqualTo(ProcessState.Idle)
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processing).isEqualTo(ProcessState.Idle)
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Failure<List<EventType>>("error")
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processing).isEqualTo(ProcessState.FailureLoading("Couldn't load event types"))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Failure<List<Course>>("error")

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processing).isEqualTo(ProcessState.FailureLoading("Couldn't load courses"))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Failure<List<EventType>>("error")
        coEvery { getCourses.invoke() } returns Result.Failure<List<Course>>("error")

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.processing).isEqualTo(ProcessState.FailureLoading("Couldn't load event types & courses"))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.SetName("name"))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.SetActivityType(ActivityType.Running))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns profile
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.SetActivityType(ActivityType.Running))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns profile
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.SetActivityType(ActivityType.Cycling))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.SetEventType(eventType))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.SetCourse(course))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.SetWater(100))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.SetRename(false))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.SetCustomWater(true))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.SetFeelAndEffort(true))
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

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)
        coEvery { saveProfile.invoke(any()) } returns Result.Success(Unit)

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.Save)
            val state = awaitItem()
            assertThat(state.processing).isEqualTo(ProcessState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Save activity - failure`() = runTest {
        val activityTypes = listOf(ActivityType.Any, ActivityType.Running)
        val eventTypes = listOf(EventType(id = 1, name = "event"))
        val courses = listOf(Course(id = 1, name = "course", type = ActivityType.Running))

        every { savedState.toRoute<Routes.EditProfile>() } returns Routes.EditProfile(1)
        coEvery { getProfile(any()) } returns null
        every { getActivityTypes.invoke() } returns activityTypes
        coEvery { getEventTypes.invoke() } returns Result.Success(eventTypes)
        coEvery { getCourses.invoke() } returns Result.Success(courses)
        coEvery { saveProfile.invoke(any()) } returns Result.Failure("error")

        viewModel = EditProfileViewModel(savedState, getProfile, getActivityTypes, getEventTypes, getCourses, saveProfile)

        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.onEvent(EditProfileEvent.Save)
            val state = awaitItem()
            assertThat(state.processing).isEqualTo(ProcessState.FailureSaving)
            cancelAndIgnoreRemainingEvents()
        }
    }
}