package paufregi.connectfeed.presentation.editprofile

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType


sealed interface EditProfileEvent {
    data class SetName(val name: String): EditProfileEvent
    data class SetActivityType(val activityType: ActivityType): EditProfileEvent
    data class SetEventType(val eventType: EventType?): EditProfileEvent
    data class SetCourse(val course: Course?): EditProfileEvent
    data class SetWater(val water: Int?): EditProfileEvent
    data class SetRename(val rename: Boolean): EditProfileEvent
    data class SetCustomWater(val customWater: Boolean): EditProfileEvent
    data class SetFeelAndEffort(val feelAndEffort: Boolean): EditProfileEvent
    data object Save: EditProfileEvent
}
