package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetLatestActivitiesUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(): Result<List<Activity>> = garminRepository.getLatestActivities(limit = 5)
}