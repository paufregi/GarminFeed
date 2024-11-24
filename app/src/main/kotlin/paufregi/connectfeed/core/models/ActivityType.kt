package paufregi.connectfeed.core.models

sealed class ActivityType(val name: String) {
    object Running : ActivityType("Running")
    object Cycling : ActivityType("Cycling")
    object Strength : ActivityType("Strength")
    object Any : ActivityType("Any")
    object Unknown : ActivityType("Unknown")

    companion object {
        fun fromName(name: String): ActivityType = when (name.lowercase()) {
            "running" -> Running
            "Cycling" -> Cycling
            "any" -> Any
            else -> Unknown
        }
    }
}
