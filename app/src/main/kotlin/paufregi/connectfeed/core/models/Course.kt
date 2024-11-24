package paufregi.connectfeed.core.models

data class Course(
    val id: Long,
    val name: String,
    val type: ActivityType
) {
    companion object {
        val commuteWork = Course(303050449, "Commute to work", ActivityType.Cycling)
        val commuteHome = Course(303050823, "Commute to home", ActivityType.Cycling)
        val ponsonbyWesthaven  = Course(314630804, "Movio - Ponsonby/Westhaven", ActivityType.Running)
        val aucklandCBD  = Course(314625811, "Auckland CBD - Water & hills", ActivityType.Running)
    }
}
