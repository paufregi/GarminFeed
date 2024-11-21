package paufregi.connectfeed.core.models

sealed class ActivityType(val name: String) {
    object Running : ActivityType("Running")
    object Cycling : ActivityType("Cycling")
    object Any : ActivityType("Any")
    object Unknown : ActivityType("Unknown")
}
