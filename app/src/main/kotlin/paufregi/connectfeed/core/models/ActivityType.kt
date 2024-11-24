package paufregi.connectfeed.core.models

sealed class ActivityType(val name: String, val order: Int) {
    object Any : ActivityType("Any", 1)
    object Running : ActivityType("Running", 2)
    object TrailRunning : ActivityType("Trail Running", 3)
    object Cycling : ActivityType("Cycling", 4)
    object Strength : ActivityType("Strength", 5)
    object Unknown : ActivityType("Unknown", 100)

    companion object {
        fun fromName(name: String): ActivityType = when (name.lowercase()) {
            "any" -> Any
            "running" -> Running
            "trail_running" -> TrailRunning
            "cycling" -> Cycling
            "strength_training" -> Strength
            else -> Unknown
        }
    }
}
