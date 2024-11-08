package paufregi.garminfeed.core.usecases

import android.util.Log
import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.Profile
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.data.repository.GarminRepository
import javax.inject.Inject

class UpdateActivityUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(
        activity: Activity?,
        profile: Profile?,
        feel: Float?,
        effort: Float?
    ):Result<Unit>{
        if (activity != null && profile != null) {
            return garminRepository.updateActivity(activity, profile, feel, effort)
        }
        return Result.Failure("Validation error")
    }
}