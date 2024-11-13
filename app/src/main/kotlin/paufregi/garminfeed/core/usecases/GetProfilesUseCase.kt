package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.core.models.ActivityType
import paufregi.garminfeed.core.models.Course
import paufregi.garminfeed.core.models.EventType
import paufregi.garminfeed.core.models.Profile
import paufregi.garminfeed.data.repository.GarminRepository
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
        )
    }
}