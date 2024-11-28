package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.ActivityType
import javax.inject.Inject

class GetActivityTypes @Inject constructor() {
    operator fun  invoke(): List<ActivityType> {
        return listOf(
            ActivityType.Any,
            ActivityType.Running,
            ActivityType.Cycling,
            ActivityType.Strength
        ).sortedBy { it.order }
    }
}