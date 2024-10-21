package paufregi.garminfeed.core.models

data class ActivityCourse(
    val id: Long,
    val name: String,
) {
    companion object {
        val work = ActivityCourse(303050449, "Commute to work")
        val home = ActivityCourse(303050823, "Commute to home")
    }
}
