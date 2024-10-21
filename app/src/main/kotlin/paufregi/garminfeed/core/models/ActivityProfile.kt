package paufregi.garminfeed.core.models

data class ActivityProfile(
    val activityName: String,
    val eventType: ActivityEventType,
    val activityType: ActivityType,
    val course: ActivityCourse,
    val water: Int
) {
    companion object {
        val presets = sequenceOf(
            ActivityProfile(
                ActivityCourse.home.name,
                ActivityEventType.transportation,
                ActivityType.Cycling,
                ActivityCourse.home,
                500),
            ActivityProfile(
                ActivityCourse.work.name,
                ActivityEventType.transportation,
                ActivityType.Cycling,
                ActivityCourse.work,
                500),
        )
    }
}
