package paufregi.garminfeed.core.models

data class Course(
    val id: Long,
    val name: String,
) {
    companion object {
        val work = Course(303050449, "Commute to work")
        val home = Course(303050823, "Commute to home")
    }
}
