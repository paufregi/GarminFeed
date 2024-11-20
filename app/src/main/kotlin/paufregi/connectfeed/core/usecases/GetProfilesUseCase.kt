package paufregi.connectfeed.core.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetProfilesUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    operator fun invoke(): Flow<List<Profile>> {
        return flowOf(listOf(
            Profile(
                name = "Commute to home",
                updateName = true,
                eventType = EventType.transportation,
                activityType = ActivityType.Cycling,
                course = Course.commuteHome,
                water = 550),
            Profile(
                name = "Commute to work",
                updateName = true,
                eventType = EventType.transportation,
                activityType = ActivityType.Cycling,
                course = Course.commuteWork,
                water = 550),
            Profile(
                name = "Ponsonby/Westhaven",
                updateName = true,
                eventType = EventType.training,
                activityType = ActivityType.Running,
                course = Course.ponsonbyWesthaven),
            Profile(
                name = "Auckland CBD",
                updateName = true,
                eventType = EventType.training,
                activityType = ActivityType.Running,
                course = Course.aucklandCBD),
        ))
    }
}