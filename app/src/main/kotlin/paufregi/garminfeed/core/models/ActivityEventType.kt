package paufregi.garminfeed.core.models

data class ActivityEventType(
    val id: Long,
    val name: String,
) {
    companion object {
        val transportation = ActivityEventType(5, "Transportation")
    }
}