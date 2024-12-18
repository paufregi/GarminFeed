package paufregi.connectfeed.presentation.profile

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType


sealed interface ProfileEvent {
    data class SetName(val name: String): ProfileEvent
    data class SetActivityType(val activityType: ActivityType): ProfileEvent
    data class SetEventType(val eventType: EventType?): ProfileEvent
    data class SetCourse(val course: Course?): ProfileEvent
    data class SetWater(val water: Int?): ProfileEvent
    data class SetRename(val rename: Boolean): ProfileEvent
    data class SetCustomWater(val customWater: Boolean): ProfileEvent
    data class SetFeelAndEffort(val feelAndEffort: Boolean): ProfileEvent
    data object Save: ProfileEvent
}
