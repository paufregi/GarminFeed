package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetLatestActivitiesUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(): Result<List<Activity>> = garminRepository.getLatestActivities(limit = 5)
}