package paufregi.garminfeed.core.models

data class Course(
    val id: Long,
    val name: String,
) {
    companion object {
        val commuteWork = Course(303050449, "Commute to work")
        val commuteHome = Course(303050823, "Commute to home")
        val ponsonbyWesthaven  = Course(314630804, "Movio - Ponsonby/Westhaven")
    }
}
