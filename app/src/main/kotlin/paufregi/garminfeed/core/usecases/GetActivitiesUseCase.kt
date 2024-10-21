package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.ActivityType
import paufregi.garminfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetActivitiesUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    operator fun invoke(): List<Activity> {
        return listOf(
            Activity(123L, "Auckland ride", ActivityType.Cycling),
            Activity(456L, "Auckland run", ActivityType.Running),
        )
    }
}