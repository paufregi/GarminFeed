package paufregi.garminfeed.core.models

data class EventType(
    val id: Long,
    val name: String,
) {
    companion object {
        val transportation = EventType(5, "Transportation")
    }
}