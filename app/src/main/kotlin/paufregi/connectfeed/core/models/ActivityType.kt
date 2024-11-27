package paufregi.connectfeed.core.models

sealed class ActivityType(val name: String, val order: Int) {
    data object Any : ActivityType("Any", 1)
    data object Running : ActivityType("Running", 2)
    data object TrailRunning : ActivityType("Trail Running", 3)
    data object Cycling : ActivityType("Cycling", 4)
    data object Strength : ActivityType("Strength", 5)
    data object Unknown : ActivityType("Unknown", 100)

    companion object {
        fun fromName(name: String?): ActivityType = when (name?.lowercase()) {
            "any" -> Any
            "running" -> Running
            "trail_running", "trail running" -> TrailRunning
            "cycling" -> Cycling
            "strength_training", "strength" -> Strength
            else -> Unknown
        }
    }
}
