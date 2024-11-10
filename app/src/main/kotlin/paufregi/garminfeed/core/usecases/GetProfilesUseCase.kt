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
                activityName = Course.home.name,
                eventType = EventType.transportation,
                activityType = ActivityType.Cycling,
                course = Course.home,
                water = 500),
            Profile(
                activityName = Course.work.name,
                eventType = EventType.transportation,
                activityType = ActivityType.Cycling,
                course = Course.work,
                water = 500),
        )
    }
}