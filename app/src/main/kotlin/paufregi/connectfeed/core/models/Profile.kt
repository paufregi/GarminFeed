package paufregi.connectfeed.core.models

data class Profile(
    val name: String,
    val updateName: Boolean,
    val eventType: EventType,
    val activityType: ActivityType,
    val course: Course,
    val water: Int? = null
)
