package paufregi.connectfeed.presentation.profile

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.presentation.ui.models.ProcessState

data class ProfileState(
    val process: ProcessState = ProcessState.Processing,
    val profile: Profile = Profile(),
    val activityTypes: List<ActivityType> = emptyList(),
    val eventTypes: List<EventType> = emptyList(),
    val courses: List<Course> = emptyList(),
)
