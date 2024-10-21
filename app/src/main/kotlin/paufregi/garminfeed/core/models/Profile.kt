package paufregi.garminfeed.core.models

data class Profile(
    val activityName: String,
    val eventType: EventType,
    val activityType: ActivityType,
    val course: Course,
    val water: Int
) {
    companion object {
        val presets = sequenceOf(
            Profile(
                Course.home.name,
                EventType.transportation,
                ActivityType.Cycling,
                Course.home,
                500),
            Profile(
                Course.work.name,
                EventType.transportation,
                ActivityType.Cycling,
                Course.work,
                500),
        )
    }
}
