package paufregi.garminfeed.core.models

sealed interface ActivityType{
    object Running : ActivityType
    object Cycling : ActivityType
    object Unknown : ActivityType
}
