package paufregi.connectfeed.presentation.profile

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile

data class EditProfileState(
    val processing: ProcessState = ProcessState.Processing,
    val profile: Profile = Profile(),
    val activityTypes: List<ActivityType> = emptyList(),
    val eventTypes: List<EventType> = emptyList(),
    val courses: List<Course> = emptyList(),
)

sealed interface ProcessState {
    data object Idle : ProcessState
    data object Processing : ProcessState
    data object Success : ProcessState
    data class FailureLoading(val reason: String) : ProcessState
    data object FailureSaving : ProcessState
}
