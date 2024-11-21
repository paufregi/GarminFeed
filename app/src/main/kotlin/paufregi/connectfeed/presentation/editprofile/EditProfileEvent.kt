package paufregi.connectfeed.presentation.editprofile

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType


sealed interface EditProfileEvent {
    data class UpdateName(val name: String): EditProfileEvent
    data class UpdateUpdateName(val updateName: Boolean): EditProfileEvent
    data class UpdateCourse(val course: Course?): EditProfileEvent
    data class UpdateEventType(val eventType: EventType?): EditProfileEvent
    data class UpdateActivityType(val activityType: ActivityType): EditProfileEvent
    data class UpdateWater(val water: Int?): EditProfileEvent
    data object Save: EditProfileEvent
}
