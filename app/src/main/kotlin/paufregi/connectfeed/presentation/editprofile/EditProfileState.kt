package paufregi.connectfeed.presentation.editprofile

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile

data class EditProfileState(
    val profile: Profile = Profile(),
    val availableActivityType: List<ActivityType> = emptyList(),
    val availableEventType: List<EventType> = emptyList(),
    val allCourses: List<Course> = emptyList(),
    val availableCourses: List<Course> = emptyList(),
)
