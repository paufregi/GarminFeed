package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.core.models.Activity
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
                Course.home.name,
                EventType.transportation,
                ActivityType.Cycling,
                Course.home,
                500),
            Profile(
                Course.work.name,
                EventType.transportation,
                ActivityType.Cycling,
                Course.work,
                500),
        )
    }
}