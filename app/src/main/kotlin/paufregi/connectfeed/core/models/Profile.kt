package paufregi.connectfeed.core.models

data class Profile(
    val id: Int? = null,
    val name: String = "",
    val updateName: Boolean = true,
    val activityType: ActivityType = ActivityType.Any,
    val eventType: EventType? = null,
    val course: Course? = null,
    val water: Int? = null
)
