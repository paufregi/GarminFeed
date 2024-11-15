package paufregi.connectfeed.core.models

data class Profile(
    val activityName: String,
    val eventType: EventType,
    val activityType: ActivityType,
    val course: Course,
    val water: Int? = null
)
