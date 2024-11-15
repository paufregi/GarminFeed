package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetProfilesUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    operator fun invoke(): List<Profile> {
        return listOf(
            Profile(
                activityName = "Commute to home",
                eventType = EventType.transportation,
                activityType = ActivityType.Cycling,
                course = Course.commuteHome,
                water = 550),
            Profile(
                activityName = "Commute to work",
                eventType = EventType.transportation,
                activityType = ActivityType.Cycling,
                course = Course.commuteWork,
                water = 550),
            Profile(
                activityName = "Ponsonby/Westhaven",
                eventType = EventType.training,
                activityType = ActivityType.Running,
                course = Course.ponsonbyWesthaven),
            Profile(
                activityName = "Auckland CBD",
                eventType = EventType.training,
                activityType = ActivityType.Running,
                course = Course.aucklandCBD),
        )
    }
}