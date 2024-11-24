package paufregi.connectfeed.presentation.editprofile

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile

data class EditProfileState(
    val processing: ProcessState = ProcessState.Processing,
    val profile: Profile = Profile(),
    val availableActivityType: List<ActivityType> = emptyList(),
    val availableEventType: List<EventType> = emptyList(),
    val allCourses: List<Course> = emptyList(),
    val availableCourses: List<Course> = emptyList(),
)

sealed interface ProcessState {
    data object Idle : ProcessState
    data object Processing : ProcessState
    data object Success : ProcessState
    data class FailureLoading(val reason: String) : ProcessState
    data object FailureUpdating : ProcessState
}
